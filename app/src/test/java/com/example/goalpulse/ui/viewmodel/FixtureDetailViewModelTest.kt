package com.example.goalpulse.ui.viewmodel

import app.cash.turbine.test
import com.example.goalpulse.data.model.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FixtureDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: FixtureDetailViewModel
    private lateinit var gson: Gson

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FixtureDetailViewModel()
        gson = Gson()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadFixture emits loading then success state`() = runTest(testDispatcher) {
        val fixture = Fixture(
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
        val fixtureJson = gson.toJson(fixture)

        viewModel.fixtureDetailState.test {
            viewModel.loadFixture(fixtureJson)
            advanceUntilIdle()

            assertTrue(awaitItem() is FixtureDetailUiState.Loading)
            advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is FixtureDetailUiState.Success)
            assertEquals(fixture.fixture?.id, (successState as FixtureDetailUiState.Success).fixture.fixture?.id)
        }
    }

    @Test
    fun `loadFixture emits error state on invalid JSON`() = runTest(testDispatcher) {
        val invalidJson = "{ invalid json }"

        viewModel.fixtureDetailState.test {
            viewModel.loadFixture(invalidJson)
            advanceUntilIdle()

            assertTrue(awaitItem() is FixtureDetailUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState is FixtureDetailUiState.Error)
            assertNotNull((errorState as FixtureDetailUiState.Error).message)
        }
    }

    @Test
    fun `loadFixture emits error state on null fixture`() = runTest(testDispatcher) {
        val nullJson = "null"

        viewModel.fixtureDetailState.test {
            viewModel.loadFixture(nullJson)
            advanceUntilIdle()

            assertTrue(awaitItem() is FixtureDetailUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState is FixtureDetailUiState.Error)
            assertEquals("Failed to parse fixture data", (errorState as FixtureDetailUiState.Error).message)
        }
    }
}
