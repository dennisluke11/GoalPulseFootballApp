package com.example.goalpulse.data.repository

import com.example.goalpulse.data.local.CacheManager
import com.example.goalpulse.data.model.Country
import com.example.goalpulse.data.model.League
import com.example.goalpulse.data.model.LeagueInfo
import com.example.goalpulse.data.model.LeagueResponse
import com.example.goalpulse.data.remote.ApiFootballService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LeaguesRepositoryTest {
    
    private lateinit var apiService: ApiFootballService
    private lateinit var cacheManager: CacheManager
    private lateinit var repository: LeaguesRepository
    private val apiKey = "test_api_key"
    
    @Before
    fun setup() {
        apiService = mockk()
        cacheManager = mockk(relaxed = true)
        repository = LeaguesRepositoryImpl(apiService, apiKey, cacheManager)
        
        every { cacheManager.generateKey(any(), any()) } returns "test_key"
        every { cacheManager.generateKey(any(), any(), any()) } returns "test_key"
        coEvery { cacheManager.get<Array<League>>(any(), any()) } returns null
        coEvery { cacheManager.put<Array<League>>(any(), any()) } returns Unit
    }
    
    @Test
    fun `searchLeagues returns cached data when available`() = runTest {
        val cachedLeagues = arrayOf(createMockLeague())
        val cacheKey = "test_key"
        
        every { cacheManager.generateKey("leagues", "search", "Premier") } returns cacheKey
        coEvery { cacheManager.get<Array<League>>(cacheKey, Array<League>::class.java) } returns cachedLeagues
        
        val result = repository.searchLeagues("Premier")
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        coVerify(exactly = 0) { apiService.getLeagues(any(), any()) }
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
            response = listOf(createMockLeague())
        )
        
        coEvery { apiService.getLeagues(apiKey, query, null) } returns mockResponse
        
        val result = repository.searchLeagues(query)
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Premier League", result.getOrNull()?.first()?.league?.name)
        coVerify { cacheManager.put<Array<League>>(any(), any()) }
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
        
        coEvery { apiService.getLeagues(apiKey, query, null) } returns mockResponse
        
        val result = repository.searchLeagues(query)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `searchLeagues returns failure when API throws exception`() = runTest {
        val query = "Premier"
        coEvery { apiService.getLeagues(apiKey, query, null) } throws Exception("Network error")
        
        val result = repository.searchLeagues(query)
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `getAllLeagues returns cached data when available`() = runTest {
        val cachedLeagues = arrayOf(createMockLeague())
        val cacheKey = "test_key"
        
        every { cacheManager.generateKey("leagues", "all") } returns cacheKey
        coEvery { cacheManager.get<Array<League>>(cacheKey, Array<League>::class.java) } returns cachedLeagues
        
        val result = repository.getAllLeagues()
        
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        coVerify(exactly = 0) { apiService.getLeagues(any()) }
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
                createMockLeague(),
                League(
                    league = LeagueInfo(2, "La Liga", "League", "logo2.png"),
                    country = Country("Spain", "ES", "flag2.png")
                )
            )
        )
        
        coEvery { apiService.getLeagues(apiKey = apiKey) } returns mockResponse
        
        val result = repository.getAllLeagues()
        
        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()?.size)
        coVerify { cacheManager.put<Array<League>>(any(), any()) }
    }
    
    @Test
    fun `getAllLeagues returns failure when API returns errors`() = runTest {
        val mockResponse = LeagueResponse(
            endpoint = "leagues",
            parameters = null,
            errors = listOf("API error"),
            results = 0,
            paging = null,
            response = null
        )
        
        coEvery { apiService.getLeagues(apiKey = apiKey) } returns mockResponse
        
        val result = repository.getAllLeagues()
        
        assertTrue(result.isFailure)
    }
    
    @Test
    fun `searchLeagues returns failure when API key is invalid`() = runTest {
        val invalidApiKey = ""
        val invalidRepository = LeaguesRepositoryImpl(apiService, invalidApiKey, cacheManager)
        
        val result = invalidRepository.searchLeagues("Premier")
        
        assertTrue(result.isFailure)
        coVerify(exactly = 0) { apiService.getLeagues(any(), any(), any()) }
    }
    
    private fun createMockLeague(): League {
        return League(
            league = LeagueInfo(1, "Premier League", "League", "logo.png"),
            country = Country("England", "GB", "flag.png")
        )
    }
}

