package com.example.goalpulse.ui.viewmodel

import app.cash.turbine.test
import com.example.goalpulse.config.AppConstants
import com.example.goalpulse.data.model.Team
import com.example.goalpulse.data.model.TeamInfo
import com.example.goalpulse.data.model.Venue
import com.example.goalpulse.data.repository.TeamsRepository
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
class TeamsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: TeamsRepository

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
    fun `searchTeams emits loading then success state`() = runTest(testDispatcher) {
        val query = "Arsenal"
        val mockTeams = createMockTeams()

        coEvery { repository.searchTeams(query) } returns Result.success(mockTeams)

        val viewModel = TeamsViewModel(repository)

        viewModel.teamsState.test {
            assertEquals(TeamsUiState.Idle, awaitItem())

            viewModel.searchTeams(query)
            advanceTimeBy(AppConstants.SEARCH_DEBOUNCE_DELAY_MS)
            advanceUntilIdle()

            assertTrue(awaitItem() is TeamsUiState.Loading)
            advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is TeamsUiState.Success)
            assertEquals(mockTeams, (successState as TeamsUiState.Success).teams)
        }

        coVerify { repository.searchTeams(query) }
    }

    @Test
    fun `searchTeams emits loading then error state on failure`() = runTest(testDispatcher) {
        val query = "Invalid"
        val errorMessage = "Network error"

        coEvery { repository.searchTeams(query) } returns Result.failure(Exception(errorMessage))

        val viewModel = TeamsViewModel(repository)

        viewModel.teamsState.test {
            assertEquals(TeamsUiState.Idle, awaitItem())

            viewModel.searchTeams(query)
            advanceTimeBy(AppConstants.SEARCH_DEBOUNCE_DELAY_MS)
            advanceUntilIdle()

            assertTrue(awaitItem() is TeamsUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState is TeamsUiState.Error)
            assertEquals(errorMessage, (errorState as TeamsUiState.Error).message)
        }
    }

    @Test
    fun `searchTeams loads popular teams when query is blank`() = runTest(testDispatcher) {
        val mockTeams = createMockTeams()

        coEvery { repository.getTeamsByLeague(AppConstants.PREMIER_LEAGUE_ID) } returns Result.success(mockTeams)

        val viewModel = TeamsViewModel(repository)

        viewModel.teamsState.test {
            assertEquals(TeamsUiState.Idle, awaitItem())

            viewModel.searchTeams("")
            advanceUntilIdle()

            assertTrue(awaitItem() is TeamsUiState.Loading)
            advanceUntilIdle()

            assertTrue(awaitItem() is TeamsUiState.Success)
        }

        coVerify { repository.getTeamsByLeague(AppConstants.PREMIER_LEAGUE_ID) }
    }

    @Test
    fun `loadPopularTeams emits loading then success state`() = runTest(testDispatcher) {
        val mockTeams = createMockTeams()

        coEvery { repository.getTeamsByLeague(AppConstants.PREMIER_LEAGUE_ID) } returns Result.success(mockTeams)

        val viewModel = TeamsViewModel(repository)

        viewModel.teamsState.test {
            assertEquals(TeamsUiState.Idle, awaitItem())

            viewModel.loadPopularTeams()
            advanceUntilIdle()

            assertTrue(awaitItem() is TeamsUiState.Loading)
            advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is TeamsUiState.Success)
            assertEquals(mockTeams, (successState as TeamsUiState.Success).teams)
        }

        coVerify { repository.getTeamsByLeague(AppConstants.PREMIER_LEAGUE_ID) }
    }

    @Test
    fun `updateSearchQuery updates search query state`() = runTest(testDispatcher) {
        val query = "Test Query"
        coEvery { repository.searchTeams(query) } returns Result.success(emptyList())

        val viewModel = TeamsViewModel(repository)

        viewModel.searchQuery.test {
            awaitItem() // Skip initial empty string

            viewModel.updateSearchQuery(query)
            advanceTimeBy(AppConstants.SEARCH_DEBOUNCE_DELAY_MS)
            advanceUntilIdle()

            assertEquals(query, awaitItem())
        }
    }

    @Test
    fun `searchTeams cancels previous job`() = runTest(testDispatcher) {
        val query1 = "Arsenal"
        val query2 = "Chelsea"

        coEvery { repository.searchTeams(any()) } returns Result.success(emptyList())

        val viewModel = TeamsViewModel(repository)

        viewModel.searchTeams(query1)
        viewModel.searchTeams(query2)

        advanceTimeBy(AppConstants.SEARCH_DEBOUNCE_DELAY_MS)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.searchTeams(query2) }
    }

    private fun createMockTeams(): List<Team> = listOf(
        Team(
            team = TeamInfo(1, "Arsenal", "ARS", "England", 1886, false, "logo.png"),
            venue = Venue(1, "Emirates Stadium", "Address", "London", 60000, "grass", "venue.png")
        )
    )
}
