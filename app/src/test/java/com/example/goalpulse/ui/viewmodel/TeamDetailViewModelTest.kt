package com.example.goalpulse.ui.viewmodel

import app.cash.turbine.test
import com.example.goalpulse.data.model.Team
import com.example.goalpulse.data.model.TeamInfo
import com.example.goalpulse.data.model.Venue
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
class TeamDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: TeamDetailViewModel
    private lateinit var gson: Gson

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TeamDetailViewModel()
        gson = Gson()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTeam emits loading then success state`() = runTest(testDispatcher) {
        val team = Team(
            team = TeamInfo(1, "Arsenal", "ARS", "England", 1886, false, "logo.png"),
            venue = Venue(1, "Emirates Stadium", "Address", "London", 60000, "grass", "venue.png")
        )
        val teamJson = gson.toJson(team)

        viewModel.teamDetailState.test {
            viewModel.loadTeam(teamJson)
            advanceUntilIdle()

            assertTrue(awaitItem() is TeamDetailUiState.Loading)
            advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is TeamDetailUiState.Success)
            assertEquals(team.team?.id, (successState as TeamDetailUiState.Success).team.team?.id)
            assertEquals(team.team?.name, successState.team.team?.name)
        }
    }

    @Test
    fun `loadTeam emits error state on invalid JSON`() = runTest(testDispatcher) {
        val invalidJson = "{ invalid json }"

        viewModel.teamDetailState.test {
            viewModel.loadTeam(invalidJson)
            advanceUntilIdle()

            assertTrue(awaitItem() is TeamDetailUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState is TeamDetailUiState.Error)
            assertNotNull((errorState as TeamDetailUiState.Error).message)
        }
    }

    @Test
    fun `loadTeam emits error state on null team`() = runTest(testDispatcher) {
        val nullJson = "null"

        viewModel.teamDetailState.test {
            viewModel.loadTeam(nullJson)
            advanceUntilIdle()

            assertTrue(awaitItem() is TeamDetailUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState is TeamDetailUiState.Error)
            assertEquals("Failed to parse team data", (errorState as TeamDetailUiState.Error).message)
        }
    }
}
