package com.example.goalpulse.data.repository

import com.example.goalpulse.data.local.CacheManager
import com.example.goalpulse.data.model.Team
import com.example.goalpulse.data.model.TeamInfo
import com.example.goalpulse.data.model.TeamResponse
import com.example.goalpulse.data.model.Venue
import com.example.goalpulse.data.remote.ApiFootballService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class TeamsRepositoryTest {
    
    private lateinit var apiService: ApiFootballService
    private lateinit var cacheManager: CacheManager
    private lateinit var repository: TeamsRepository
    private val apiKey = "test_api_key"
    
    @Before
    fun setup() {
        apiService = mockk()
        cacheManager = mockk(relaxed = true)
        repository = TeamsRepositoryImpl(apiService, apiKey, cacheManager)
        
        every { cacheManager.generateKey(any(), any()) } returns "test_key"
        every { cacheManager.generateKey(any(), any(), any()) } returns "test_key"
        every { cacheManager.generateKey(any(), any(), any(), any()) } returns "test_key"
        coEvery { cacheManager.get<Array<Team>>(any(), any()) } returns null
        coEvery { cacheManager.put<Array<Team>>(any(), any()) } returns Unit
    }
    
    @Test
    fun `searchTeams returns cached data when available`() = runTest {
        val cachedTeams = arrayOf(createMockTeam())
        val cacheKey = "test_key"
        
        every { cacheManager.generateKey("teams", "search", "Arsenal") } returns cacheKey
        coEvery { cacheManager.get<Array<Team>>(cacheKey, Array<Team>::class.java) } returns cachedTeams
        
        val result = repository.searchTeams("Arsenal")
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        coVerify(exactly = 0) { apiService.searchTeams(any(), any()) }
    }
    
    @Test
    fun `searchTeams returns success when API call succeeds`() = runTest {
        val query = "Arsenal"
        val mockResponse = TeamResponse(
            endpoint = "teams",
            parameters = mapOf("name" to query),
            errors = null,
            results = 1,
            paging = null,
            response = listOf(createMockTeam())
        )
        
        coEvery { apiService.searchTeams(apiKey, query) } returns mockResponse
        
        val result = repository.searchTeams(query)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Arsenal", result.getOrNull()?.first()?.team?.name)
        coVerify { cacheManager.put<Array<Team>>(any(), any()) }
    }
    
    @Test
    fun `searchTeams returns failure when API returns errors`() = runTest {
        val query = "Invalid"
        val mockResponse = TeamResponse(
            endpoint = "teams",
            parameters = mapOf("name" to query),
            errors = listOf("Invalid search"),
            results = 0,
            paging = null,
            response = null
        )
        
        coEvery { apiService.searchTeams(apiKey, query) } returns mockResponse
        
        val result = repository.searchTeams(query)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `searchTeams returns failure when API throws exception`() = runTest {
        val query = "Arsenal"
        coEvery { apiService.searchTeams(apiKey, query) } throws Exception("Network error")
        
        val result = repository.searchTeams(query)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getTeamsByLeague returns cached data when available`() = runTest {
        val cachedTeams = arrayOf(createMockTeam())
        val cacheKey = "test_key"
        val leagueId = 39
        val season = 2024
        
        every { cacheManager.generateKey("teams", "league", leagueId, season) } returns cacheKey
        coEvery { cacheManager.get<Array<Team>>(cacheKey, Array<Team>::class.java) } returns cachedTeams
        
        val result = repository.getTeamsByLeague(leagueId, season)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        coVerify(exactly = 0) { apiService.getTeamsByLeague(any(), any(), any()) }
    }
    
    @Test
    fun `getTeamsByLeague returns success when API call succeeds`() = runTest {
        val leagueId = 39
        val season = 2024
        val mockResponse = TeamResponse(
            endpoint = "teams",
            parameters = mapOf("league" to leagueId.toString(), "season" to season.toString()),
            errors = null,
            results = 1,
            paging = null,
            response = listOf(createMockTeam())
        )
        
        coEvery { 
            apiService.getTeamsByLeague(apiKey, leagueId, season)
        } returns Response.success(mockResponse)
        
        val result = repository.getTeamsByLeague(leagueId, season)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        coVerify { cacheManager.put<Array<Team>>(any(), any()) }
    }
    
    @Test
    fun `getTeamsByLeague returns failure when API returns error response`() = runTest {
        val leagueId = 39
        val season = 2024
        
        coEvery { 
            apiService.getTeamsByLeague(apiKey, leagueId, season)
        } returns Response.error(404, "Not found".toResponseBody())
        
        val result = repository.getTeamsByLeague(leagueId, season)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getTeamsByLeague returns failure when API throws exception`() = runTest {
        val leagueId = 39
        val season = 2024
        
        coEvery { 
            apiService.getTeamsByLeague(apiKey, leagueId, season)
        } throws Exception("Network error")
        
        val result = repository.getTeamsByLeague(leagueId, season)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `searchTeams returns failure when API key is invalid`() = runTest {
        val invalidApiKey = ""
        val invalidRepository = TeamsRepositoryImpl(apiService, invalidApiKey, cacheManager)
        
        val result = invalidRepository.searchTeams("Arsenal")
        
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { apiService.searchTeams(any(), any()) }
    }
    
    private fun createMockTeam(): Team {
        return Team(
            team = TeamInfo(1, "Arsenal", "ARS", "England", 1886, false, "logo.png"),
            venue = Venue(1, "Emirates Stadium", "Address", "London", 60000, "grass", "venue.png")
        )
    }
}

