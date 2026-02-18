package com.example.goalpulse.ui.viewmodel

import app.cash.turbine.test
import com.example.goalpulse.data.model.*
import com.example.goalpulse.data.repository.FixturesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
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
class FixturesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FixturesRepository

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
    fun `loadFixtures emits loading then success state`() = runTest(testDispatcher) {
        val mockFixtures = createMockFixtures()
        val leagueId = 39

        coEvery { repository.getFixtures(leagueId, any(), any(), any()) } returns Result.success(mockFixtures)

        val viewModel = FixturesViewModel(repository)

        viewModel.fixturesState.test {
            assertEquals(FixturesUiState.Idle, awaitItem())

            viewModel.loadFixtures(leagueId = leagueId)
            advanceUntilIdle()

            assertTrue(awaitItem() is FixturesUiState.Loading)
            advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is FixturesUiState.Success)
            assertEquals(mockFixtures, (successState as FixturesUiState.Success).fixtures)
        }

        coVerify { repository.getFixtures(leagueId, null, null, null) }
    }

    @Test
    fun `loadFixtures emits loading then error state on failure`() = runTest(testDispatcher) {
        val errorMessage = "Network error"
        val leagueId = 39

        coEvery { repository.getFixtures(any(), any(), any(), any()) } returns Result.failure(Exception(errorMessage))

        val viewModel = FixturesViewModel(repository)

        viewModel.fixturesState.test {
            assertEquals(FixturesUiState.Idle, awaitItem())

            viewModel.loadFixtures(leagueId = leagueId)
            advanceUntilIdle()

            assertTrue(awaitItem() is FixturesUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState is FixturesUiState.Error)
            assertEquals(errorMessage, (errorState as FixturesUiState.Error).message)
        }
    }

    @Test
    fun `loadFixtures with all parameters`() = runTest(testDispatcher) {
        val mockFixtures = createMockFixtures()
        val leagueId = 39
        val season = 2024
        val teamId = 1
        val date = "2024-01-01"

        coEvery { repository.getFixtures(leagueId, season, teamId, date) } returns Result.success(mockFixtures)

        val viewModel = FixturesViewModel(repository)

        viewModel.fixturesState.test {
            assertEquals(FixturesUiState.Idle, awaitItem())

            viewModel.loadFixtures(leagueId, season, teamId, date)
            advanceUntilIdle()

            assertTrue(awaitItem() is FixturesUiState.Loading)
            advanceUntilIdle()

            assertTrue(awaitItem() is FixturesUiState.Success)
        }

        coVerify { repository.getFixtures(leagueId, season, teamId, date) }
    }

    @Test
    fun `loadFixtures cancels previous job`() = runTest(testDispatcher) {
        val leagueId1 = 39
        val leagueId2 = 140

        coEvery { repository.getFixtures(any(), any(), any(), any()) } returns Result.success(emptyList())

        val viewModel = FixturesViewModel(repository)

        viewModel.loadFixtures(leagueId = leagueId1)
        viewModel.loadFixtures(leagueId = leagueId2)

        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getFixtures(leagueId2, null, null, null) }
    }

    private fun createMockFixtures(): List<Fixture> = listOf(
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
}
