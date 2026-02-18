package com.example.goalpulse.data.repository

import com.example.goalpulse.data.local.CacheManager
import com.example.goalpulse.data.model.*
import com.example.goalpulse.data.remote.ApiFootballService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FixturesRepositoryTest {
    
    private lateinit var apiService: ApiFootballService
    private lateinit var cacheManager: CacheManager
    private lateinit var repository: FixturesRepository
    private val apiKey = "test_api_key"
    
    @Before
    fun setup() {
        apiService = mockk()
        cacheManager = mockk(relaxed = true)
        repository = FixturesRepositoryImpl(apiService, apiKey, cacheManager)
        
        every { cacheManager.generateKey(any(), any(), any(), any(), any()) } returns "test_key"
        coEvery { cacheManager.get<Array<Fixture>>(any(), any()) } returns null
        coEvery { cacheManager.put<Array<Fixture>>(any(), any()) } returns Unit
    }
    
    @Test
    fun `getFixtures returns cached data when available`() = runTest {
        val cachedFixtures = arrayOf(createMockFixture())
        val cacheKey = "test_key"
        val leagueId = 39
        
        every { cacheManager.generateKey("fixtures", leagueId, any(), any(), any()) } returns cacheKey
        coEvery { cacheManager.get<Array<Fixture>>(cacheKey, Array<Fixture>::class.java) } returns cachedFixtures
        
        val result = repository.getFixtures(leagueId = leagueId)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        coVerify(exactly = 0) { apiService.getFixtures(any(), any(), any(), any()) }
    }
    
    @Test
    fun `getFixtures returns success when API call succeeds`() = runTest {
        val leagueId = 39
        val mockResponse = FixtureResponse(
            endpoint = "fixtures",
            parameters = mapOf("league" to leagueId.toString()),
            errors = null,
            results = 1,
            paging = null,
            response = listOf(createMockFixture())
        )
        
        coEvery { 
            apiService.getFixtures(apiKey, leagueId, any(), any(), any())
        } returns mockResponse
        
        val result = repository.getFixtures(leagueId = leagueId)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Arsenal", result.getOrNull()?.first()?.teams?.home?.name)
        coVerify { cacheManager.put<Array<Fixture>>(any(), any()) }
    }
    
    @Test
    fun `getFixtures returns failure when API returns errors`() = runTest {
        val leagueId = 39
        val mockResponse = FixtureResponse(
            endpoint = "fixtures",
            parameters = mapOf("league" to leagueId.toString()),
            errors = listOf("API error"),
            results = 0,
            paging = null,
            response = null
        )
        
        coEvery { 
            apiService.getFixtures(apiKey, leagueId, any(), any(), any())
        } returns mockResponse
        
        val result = repository.getFixtures(leagueId = leagueId)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getFixtures returns failure when API throws exception`() = runTest {
        val leagueId = 39
        
        coEvery { 
            apiService.getFixtures(apiKey, leagueId, any(), any(), any())
        } throws Exception("Network error")
        
        val result = repository.getFixtures(leagueId = leagueId)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getFixtures with all parameters`() = runTest {
        val leagueId = 39
        val season = 2024
        val teamId = 1
        val date = "2024-01-01"
        val mockResponse = FixtureResponse(
            endpoint = "fixtures",
            parameters = mapOf(
                "league" to leagueId.toString(),
                "season" to season.toString(),
                "team" to teamId.toString(),
                "date" to date
            ),
            errors = null,
            results = 1,
            paging = null,
            response = listOf(createMockFixture())
        )
        
        coEvery { 
            apiService.getFixtures(apiKey, leagueId, season, teamId, date)
        } returns mockResponse
        
        val result = repository.getFixtures(leagueId, season, teamId, date)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        coVerify { cacheManager.put<Array<Fixture>>(any(), any()) }
    }
    
    @Test
    fun `getFixtures returns failure when API key is invalid`() = runTest {
        val invalidApiKey = ""
        val invalidRepository = FixturesRepositoryImpl(apiService, invalidApiKey, cacheManager)
        
        val result = invalidRepository.getFixtures(leagueId = 39)
        
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { apiService.getFixtures(any(), any(), any(), any(), any()) }
    }
    
    private fun createMockFixture(): Fixture {
        return Fixture(
            fixture = FixtureInfo(
                id = 1,
                referee = "Referee",
                timezone = "UTC",
                date = "2024-01-01T15:00:00",
                timestamp = 1704114000,
                venue = Venue(1, "Stadium", "Address", "City", 50000, "grass", "venue.png"),
                status = Status("Finished", "FT", 90)
            ),
            league = LeagueInfo(1, "Premier League", "League", "logo.png"),
            teams = Teams(
                home = TeamInfo(1, "Arsenal", "ARS", "England", 1886, false, "logo1.png"),
                away = TeamInfo(2, "Chelsea", "CHE", "England", 1905, false, "logo2.png")
            ),
            goals = Goals(2, 1),
            score = Score(
                fulltime = ScoreDetail(2, 1),
                halftime = ScoreDetail(1, 0)
            )
        )
    }
}

