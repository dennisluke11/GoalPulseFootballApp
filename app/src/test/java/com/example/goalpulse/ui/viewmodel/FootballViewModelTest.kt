package com.example.goalpulse.ui.viewmodel

import app.cash.turbine.test
import com.example.goalpulse.data.model.*
import com.example.goalpulse.data.repository.FootballRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FootballViewModelTest {
    
    private lateinit var repository: FootballRepository
    private lateinit var viewModel: FootballViewModel
    
    @Before
    fun setup() {
        repository = mockk()
        coEvery { repository.getAllLeagues() } returns Result.success(emptyList())
        viewModel = FootballViewModel(repository)
    }
    
    @Test
    fun `searchLeagues emits loading then success state`() = runTest {
        val query = "Premier"
        val mockLeagues = listOf(
            League(
                league = LeagueInfo(1, "Premier League", "League", "logo.png"),
                country = Country("England", "GB", "flag.png")
            )
        )
        
        coEvery { repository.searchLeagues(query) } returns Result.success(mockLeagues)
        
        viewModel.leaguesState.test {
            viewModel.searchLeagues(query)
            
            val loadingState = awaitItem()
            assertTrue(loadingState is LeaguesUiState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is LeaguesUiState.Success)
            assertEquals(mockLeagues, (successState as LeaguesUiState.Success).leagues)
        }
    }
    
    @Test
    fun `searchLeagues emits loading then error state on failure`() = runTest {
        val query = "Invalid"
        val errorMessage = "Network error"
        
        coEvery { repository.searchLeagues(query) } returns Result.failure(Exception(errorMessage))
        
        viewModel.leaguesState.test {
            viewModel.searchLeagues(query)
            
            val loadingState = awaitItem()
            assertTrue(loadingState is LeaguesUiState.Loading)
            
            val errorState = awaitItem()
            assertTrue(errorState is LeaguesUiState.Error)
            assertEquals(errorMessage, (errorState as LeaguesUiState.Error).message)
        }
    }
    
    @Test
    fun `searchLeagues loads all leagues when query is blank`() = runTest {
        val mockLeagues = listOf(
            League(
                league = LeagueInfo(1, "Premier League", "League", "logo.png"),
                country = Country("England", "GB", "flag.png")
            )
        )
        
        coEvery { repository.getAllLeagues() } returns Result.success(mockLeagues)
        
        viewModel.leaguesState.test {
            viewModel.searchLeagues("")
            
            val loadingState = awaitItem()
            assertTrue(loadingState is LeaguesUiState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is LeaguesUiState.Success)
        }
    }
    
    @Test
    fun `searchTeams emits loading then success state`() = runTest {
        val query = "Arsenal"
        val mockTeams = listOf(
            Team(
                team = TeamInfo(1, "Arsenal", "ARS", "England", 1886, false, "logo.png"),
                venue = Venue(1, "Emirates Stadium", "Address", "London", 60000, "grass", "venue.png")
            )
        )
        
        coEvery { repository.searchTeams(query) } returns Result.success(mockTeams)
        
        viewModel.teamsState.test {
            viewModel.searchTeams(query)
            
            val loadingState = awaitItem()
            assertTrue(loadingState is TeamsUiState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is TeamsUiState.Success)
            assertEquals(mockTeams, (successState as TeamsUiState.Success).teams)
        }
    }
    
    @Test
    fun `searchTeams emits idle state when query is blank`() = runTest {
        viewModel.teamsState.test {
            viewModel.searchTeams("")
            
            val idleState = awaitItem()
            assertTrue(idleState is TeamsUiState.Idle)
        }
    }
    
    @Test
    fun `loadFixtures emits loading then success state`() = runTest {
        val mockFixtures = listOf(
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
        
        coEvery { repository.getFixtures(null, null, null) } returns Result.success(mockFixtures)
        
        viewModel.fixturesState.test {
            viewModel.loadFixtures()
            
            val loadingState = awaitItem()
            assertTrue(loadingState is FixturesUiState.Loading)
            
            val successState = awaitItem()
            assertTrue(successState is FixturesUiState.Success)
            assertEquals(mockFixtures, (successState as FixturesUiState.Success).fixtures)
        }
    }
    
    @Test
    fun `updateSearchQuery updates search query state`() = runTest {
        val query = "Test Query"
        
        viewModel.updateSearchQuery(query)
        
        viewModel.searchQuery.test {
            assertEquals(query, awaitItem())
        }
    }
}

