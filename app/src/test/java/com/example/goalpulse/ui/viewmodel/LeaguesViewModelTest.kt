package com.example.goalpulse.ui.viewmodel

import app.cash.turbine.test
import com.example.goalpulse.config.AppConstants
import com.example.goalpulse.data.model.Country
import com.example.goalpulse.data.model.League
import com.example.goalpulse.data.model.LeagueInfo
import com.example.goalpulse.data.repository.LeaguesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeaguesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: LeaguesRepository
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state loads all leagues`() = runTest(testDispatcher) {
        val mockLeagues = createMockLeagues()
        coEvery { repository.getAllLeagues() } returns Result.success(mockLeagues)

        val viewModel = LeaguesViewModel(repository)

        viewModel.leaguesState.test {
            assertEquals(LeaguesUiState.Idle, awaitItem())
            advanceUntilIdle()

            val loadingState = awaitItem()
            assertTrue(loadingState is LeaguesUiState.Loading)
            advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is LeaguesUiState.Success)
            assertEquals(mockLeagues, (successState as LeaguesUiState.Success).leagues)
        }

        coVerify { repository.getAllLeagues() }
    }
    
    @Test
    fun `searchLeagues emits loading then success state`() = runTest(testDispatcher) {
        val query = "Premier"
        val mockLeagues = createMockLeagues()

        coEvery { repository.searchLeagues(query) } returns Result.success(mockLeagues)
        coEvery { repository.getAllLeagues() } returns Result.success(emptyList())

        val viewModel = LeaguesViewModel(repository)

        viewModel.leaguesState.test {
            awaitItem() // Idle
            advanceUntilIdle()
            awaitItem() // Loading from init
            advanceUntilIdle()
            awaitItem() // Success from init

            viewModel.searchLeagues(query)
            advanceTimeBy(AppConstants.SEARCH_DEBOUNCE_DELAY_MS)
            advanceUntilIdle()

            assertTrue(awaitItem() is LeaguesUiState.Loading)
            advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is LeaguesUiState.Success)
            assertEquals(mockLeagues, (successState as LeaguesUiState.Success).leagues)
        }

        coVerify { repository.searchLeagues(query) }
    }
    
    @Test
    fun `searchLeagues emits loading then error state on failure`() = runTest(testDispatcher) {
        val query = "Invalid"
        val errorMessage = "Network error"

        coEvery { repository.searchLeagues(query) } returns Result.failure(Exception(errorMessage))
        coEvery { repository.getAllLeagues() } returns Result.success(emptyList())

        val viewModel = LeaguesViewModel(repository)

        viewModel.leaguesState.test {
            awaitItem() // Idle
            advanceUntilIdle()
            awaitItem() // Loading from init
            advanceUntilIdle()
            awaitItem() // Success from init

            viewModel.searchLeagues(query)
            advanceTimeBy(AppConstants.SEARCH_DEBOUNCE_DELAY_MS)
            advanceUntilIdle()

            assertTrue(awaitItem() is LeaguesUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState is LeaguesUiState.Error)
            assertEquals(errorMessage, (errorState as LeaguesUiState.Error).message)
        }
    }
    
    @Test
    fun `searchLeagues loads all leagues when query is blank`() = runTest(testDispatcher) {
        val mockLeagues = createMockLeagues()

        coEvery { repository.getAllLeagues() } returns Result.success(mockLeagues)

        val viewModel = LeaguesViewModel(repository)

        viewModel.leaguesState.test {
            awaitItem() // Idle
            advanceUntilIdle()
            awaitItem() // Loading from init
            advanceUntilIdle()
            awaitItem() // Success from init

            viewModel.searchLeagues("")
            advanceUntilIdle()

            assertTrue(awaitItem() is LeaguesUiState.Loading)
            advanceUntilIdle()

            assertTrue(awaitItem() is LeaguesUiState.Success)
        }

        coVerify(exactly = 2) { repository.getAllLeagues() }
    }
    
    @Test
    fun `updateSearchQuery updates search query state`() = runTest(testDispatcher) {
        val query = "Test Query"
        coEvery { repository.searchLeagues(query) } returns Result.success(emptyList())
        coEvery { repository.getAllLeagues() } returns Result.success(emptyList())

        val viewModel = LeaguesViewModel(repository)

        viewModel.searchQuery.test {
            awaitItem() // Skip initial empty string

            viewModel.updateSearchQuery(query)
            advanceTimeBy(AppConstants.SEARCH_DEBOUNCE_DELAY_MS)
            advanceUntilIdle()

            assertEquals(query, awaitItem())
        }
    }
    
    @Test
    fun `searchLeagues cancels previous job`() = runTest(testDispatcher) {
        val query1 = "Premier"
        val query2 = "La Liga"

        coEvery { repository.searchLeagues(any()) } returns Result.success(emptyList())
        coEvery { repository.getAllLeagues() } returns Result.success(emptyList())

        val viewModel = LeaguesViewModel(repository)

        viewModel.searchLeagues(query1)
        viewModel.searchLeagues(query2)

        advanceTimeBy(AppConstants.SEARCH_DEBOUNCE_DELAY_MS)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.searchLeagues(query2) }
    }
    
    @Test
    fun `loadAllLeagues emits loading then success state`() = runTest(testDispatcher) {
        val mockLeagues = createMockLeagues()
        coEvery { repository.getAllLeagues() } returns Result.success(mockLeagues)

        val viewModel = LeaguesViewModel(repository)

        viewModel.leaguesState.test {
            awaitItem() // Idle
            advanceUntilIdle()
            awaitItem() // Loading from init
            advanceUntilIdle()
            awaitItem() // Success from init

            viewModel.loadAllLeagues()
            advanceUntilIdle()

            assertTrue(awaitItem() is LeaguesUiState.Loading)
            advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is LeaguesUiState.Success)
            assertEquals(mockLeagues, (successState as LeaguesUiState.Success).leagues)
        }
    }

    private fun createMockLeagues(): List<League> = listOf(
        League(
            league = LeagueInfo(1, "Premier League", "League", "logo.png"),
            country = Country("England", "GB", "flag.png")
        )
    )
}

