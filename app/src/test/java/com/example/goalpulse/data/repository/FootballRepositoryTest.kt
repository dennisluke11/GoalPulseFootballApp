package com.example.goalpulse.data.repository

import com.example.goalpulse.data.model.*
import com.example.goalpulse.data.remote.ApiFootballService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FootballRepositoryTest {
    
    private lateinit var apiService: ApiFootballService
    private lateinit var repository: FootballRepository
    private val apiKey = "test_api_key"
    
    @Before
    fun setup() {
        apiService = mockk()
        repository = FootballRepositoryImpl(apiService, apiKey)
    }
    
    @Test
    fun `searchLeagues returns success when API call succeeds`() = runTest {
        val query = "Premier"
        val mockResponse = LeagueResponse(
            endpoint = "leagues",
            parameters = mapOf("search" to query),
            errors = null,
            results = 1,
            paging = null,
            response = listOf(
                League(
                    league = LeagueInfo(1, "Premier League", "League", "logo.png"),
                    country = Country("England", "GB", "flag.png")
                )
            )
        )
        
        coEvery {
            apiService.getLeagues(apiKey, "api-football-v1.p.rapidapi.com", query, null)
        } returns mockResponse
        
        val result = repository.searchLeagues(query)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Premier League", result.getOrNull()?.first()?.league?.name)
    }
    
    @Test
    fun `searchLeagues returns failure when API returns errors`() = runTest {
        val query = "Invalid"
        val mockResponse = LeagueResponse(
            endpoint = "leagues",
            parameters = mapOf("search" to query),
            errors = listOf("Invalid search"),
            results = 0,
            paging = null,
            response = null
        )
        
        coEvery {
            apiService.getLeagues(apiKey, "api-football-v1.p.rapidapi.com", query, null)
        } returns mockResponse
        
        val result = repository.searchLeagues(query)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `searchLeagues returns failure when API throws exception`() = runTest {
        val query = "Premier"
        coEvery {
            apiService.getLeagues(apiKey, "api-football-v1.p.rapidapi.com", query, null)
        } throws Exception("Network error")
        
        val result = repository.searchLeagues(query)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `searchTeams returns success when API call succeeds`() = runTest {
        val query = "Arsenal"
        val mockResponse = TeamResponse(
            endpoint = "teams",
            parameters = mapOf("search" to query),
            errors = null,
            results = 1,
            paging = null,
            response = listOf(
                Team(
                    team = TeamInfo(1, "Arsenal", "ARS", "England", 1886, false, "logo.png"),
                    venue = Venue(1, "Emirates Stadium", "Address", "London", 60000, "grass", "venue.png")
                )
            )
        )
        
        coEvery {
            apiService.searchTeams(apiKey, "api-football-v1.p.rapidapi.com", query)
        } returns mockResponse
        
        val result = repository.searchTeams(query)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Arsenal", result.getOrNull()?.first()?.team?.name)
    }
    
    @Test
    fun `getAllLeagues returns success when API call succeeds`() = runTest {
        val mockResponse = LeagueResponse(
            endpoint = "leagues",
            parameters = null,
            errors = null,
            results = 2,
            paging = null,
            response = listOf(
                League(
                    league = LeagueInfo(1, "Premier League", "League", "logo1.png"),
                    country = Country("England", "GB", "flag1.png")
                ),
                League(
                    league = LeagueInfo(2, "La Liga", "League", "logo2.png"),
                    country = Country("Spain", "ES", "flag2.png")
                )
            )
        )
        
        coEvery {
            apiService.getLeagues(apiKey, "api-football-v1.p.rapidapi.com", null, null)
        } returns mockResponse
        
        val result = repository.getAllLeagues()
        
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
    }
    
    @Test
    fun `getFixtures returns success when API call succeeds`() = runTest {
        val mockResponse = FixtureResponse(
            endpoint = "fixtures",
            parameters = null,
            errors = null,
            results = 1,
            paging = null,
            response = listOf(
                Fixture(
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
            )
        )
        
        coEvery {
            apiService.getFixtures(apiKey, "api-football-v1.p.rapidapi.com", null, null, null)
        } returns mockResponse
        
        val result = repository.getFixtures()
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Arsenal", result.getOrNull()?.first()?.teams?.home?.name)
    }
}

