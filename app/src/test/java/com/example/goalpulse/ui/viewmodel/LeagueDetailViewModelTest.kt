package com.example.goalpulse.ui.viewmodel

import app.cash.turbine.test
import com.example.goalpulse.data.model.Country
import com.example.goalpulse.data.model.League
import com.example.goalpulse.data.model.LeagueInfo
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
class LeagueDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: LeagueDetailViewModel
    private lateinit var gson: Gson

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LeagueDetailViewModel()
        gson = Gson()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadLeague emits loading then success state`() = runTest(testDispatcher) {
        val league = League(
            league = LeagueInfo(1, "Premier League", "League", "logo.png"),
            country = Country("England", "GB", "flag.png")
        )
        val leagueJson = gson.toJson(league)

        viewModel.leagueDetailState.test {
            viewModel.loadLeague(leagueJson)
            advanceUntilIdle()

            assertTrue(awaitItem() is LeagueDetailUiState.Loading)
            advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is LeagueDetailUiState.Success)
            assertEquals(league.league?.id, (successState as LeagueDetailUiState.Success).league.league?.id)
            assertEquals(league.league?.name, successState.league.league?.name)
        }
    }

    @Test
    fun `loadLeague emits error state on invalid JSON`() = runTest(testDispatcher) {
        val invalidJson = "{ invalid json }"

        viewModel.leagueDetailState.test {
            viewModel.loadLeague(invalidJson)
            advanceUntilIdle()

            assertTrue(awaitItem() is LeagueDetailUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState is LeagueDetailUiState.Error)
            assertNotNull((errorState as LeagueDetailUiState.Error).message)
        }
    }

    @Test
    fun `loadLeague emits error state on null league`() = runTest(testDispatcher) {
        val nullJson = "null"

        viewModel.leagueDetailState.test {
            viewModel.loadLeague(nullJson)
            advanceUntilIdle()

            assertTrue(awaitItem() is LeagueDetailUiState.Loading)
            advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState is LeagueDetailUiState.Error)
            assertEquals("Failed to parse league data", (errorState as LeagueDetailUiState.Error).message)
        }
    }
}
