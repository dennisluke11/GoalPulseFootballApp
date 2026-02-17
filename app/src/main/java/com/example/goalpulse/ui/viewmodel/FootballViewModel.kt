package com.example.goalpulse.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalpulse.data.model.Fixture
import com.example.goalpulse.data.model.League
import com.example.goalpulse.data.model.Team
import com.example.goalpulse.data.repository.FootballRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FootballViewModel(
    private val repository: FootballRepository
) : ViewModel() {
    
    private val _leaguesState = MutableStateFlow<LeaguesUiState>(LeaguesUiState.Idle)
    val leaguesState: StateFlow<LeaguesUiState> = _leaguesState.asStateFlow()
    
    private val _teamsState = MutableStateFlow<TeamsUiState>(TeamsUiState.Idle)
    val teamsState: StateFlow<TeamsUiState> = _teamsState.asStateFlow()
    
    private val _fixturesState = MutableStateFlow<FixturesUiState>(FixturesUiState.Idle)
    val fixturesState: StateFlow<FixturesUiState> = _fixturesState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private var searchLeaguesJob: Job? = null
    private var searchTeamsJob: Job? = null
    private var loadFixturesJob: Job? = null
    private var loadPopularTeamsJob: Job? = null
    
    init {
        loadAllLeagues()
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun searchLeagues(query: String) {
        searchLeaguesJob?.cancel()
        
        if (query.isBlank()) {
            loadAllLeagues()
            return
        }
        
        searchLeaguesJob = viewModelScope.launch {
            try {
                delay(300)
                ensureActive()
                _leaguesState.value = LeaguesUiState.Loading
                repository.searchLeagues(query)
                    .onSuccess { leagues ->
                        ensureActive()
                        _leaguesState.value = LeaguesUiState.Success(leagues)
                    }
                    .onFailure { error ->
                        ensureActive()
                        _leaguesState.value = LeaguesUiState.Error(error.message ?: "Unknown error")
                    }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                if (e !is CancellationException && e !is IllegalStateException) {
                    try {
                        ensureActive()
                        _leaguesState.value = LeaguesUiState.Error(e.message ?: "Unknown error")
                    } catch (ce: CancellationException) {
                    }
                }
            }
        }
    }
    
    fun loadAllLeagues() {
        viewModelScope.launch {
            try {
                _leaguesState.value = LeaguesUiState.Loading
                repository.getAllLeagues()
                    .onSuccess { leagues ->
                        ensureActive()
                        _leaguesState.value = LeaguesUiState.Success(leagues)
                    }
                    .onFailure { error ->
                        ensureActive()
                        _leaguesState.value = LeaguesUiState.Error(error.message ?: "Unknown error")
                    }
            } catch (e: CancellationException) {
            } catch (e: Exception) {
                if (e !is CancellationException && e !is IllegalStateException) {
                    try {
                        ensureActive()
                        _leaguesState.value = LeaguesUiState.Error(e.message ?: "Unknown error")
                    } catch (ce: CancellationException) {
                    }
                }
            }
        }
    }
    
    fun searchTeams(query: String) {
        searchTeamsJob?.cancel()
        loadPopularTeamsJob?.cancel()
        
        if (query.isBlank()) {
            loadPopularTeams()
            return
        }
        
        searchTeamsJob = viewModelScope.launch {
            try {
                delay(300)
                if (!coroutineContext.isActive) return@launch
                _teamsState.value = TeamsUiState.Loading
                repository.searchTeams(query)
                    .onSuccess { teams ->
                        if (coroutineContext.isActive) {
                            _teamsState.value = TeamsUiState.Success(teams)
                        }
                    }
                    .onFailure { error ->
                        if (coroutineContext.isActive) {
                            _teamsState.value = TeamsUiState.Error(error.message ?: "Unknown error")
                        }
                    }
            } catch (e: CancellationException) {
            } catch (e: IllegalStateException) {
            } catch (e: Exception) {
                if (coroutineContext.isActive) {
                    try {
                        _teamsState.value = TeamsUiState.Error(e.message ?: "Unknown error")
                    } catch (ex: Exception) {
                    }
                }
            }
        }
    }
    
    fun loadTeamsByLeague(leagueId: Int) {
        loadPopularTeamsJob?.cancel()
        searchTeamsJob?.cancel()
        viewModelScope.launch {
            try {
                if (!coroutineContext.isActive) return@launch
                _teamsState.value = TeamsUiState.Loading
                repository.getTeamsByLeague(leagueId)
                    .onSuccess { teams ->
                        if (coroutineContext.isActive) {
                            _teamsState.value = TeamsUiState.Success(teams)
                        }
                    }
                    .onFailure { error ->
                        if (coroutineContext.isActive) {
                            _teamsState.value = TeamsUiState.Error(error.message ?: "Unknown error")
                        }
                    }
            } catch (e: CancellationException) {
            } catch (e: IllegalStateException) {
            } catch (e: Exception) {
                if (coroutineContext.isActive) {
                    try {
                        _teamsState.value = TeamsUiState.Error(e.message ?: "Unknown error")
                    } catch (ex: Exception) {
                    }
                }
            }
        }
    }
    
    fun loadPopularTeams() {
        loadPopularTeamsJob?.cancel()
        searchTeamsJob?.cancel()
        loadPopularTeamsJob = viewModelScope.launch {
            try {
                if (!coroutineContext.isActive) return@launch
                _teamsState.value = TeamsUiState.Loading
                repository.getTeamsByLeague(39)
                    .onSuccess { teams ->
                        if (coroutineContext.isActive) {
                            _teamsState.value = TeamsUiState.Success(teams)
                        }
                    }
                    .onFailure { error ->
                        if (coroutineContext.isActive) {
                            _teamsState.value = TeamsUiState.Error(error.message ?: "Unknown error")
                        }
                    }
            } catch (e: CancellationException) {
            } catch (e: IllegalStateException) {
            } catch (e: Exception) {
                if (coroutineContext.isActive) {
                    try {
                        _teamsState.value = TeamsUiState.Error(e.message ?: "Unknown error")
                    } catch (ex: Exception) {
                    }
                }
            }
        }
    }
    
    fun loadFixtures(leagueId: Int? = null, season: Int? = null, teamId: Int? = null, date: String? = null) {
        loadFixturesJob?.cancel()
        loadFixturesJob = viewModelScope.launch {
            try {
                if (!coroutineContext.isActive) return@launch
                _fixturesState.value = FixturesUiState.Loading
                repository.getFixtures(leagueId, season, teamId, date)
                    .onSuccess { fixtures ->
                        if (coroutineContext.isActive) {
                            _fixturesState.value = FixturesUiState.Success(fixtures)
                        }
                    }
                    .onFailure { error ->
                        if (coroutineContext.isActive) {
                            _fixturesState.value = FixturesUiState.Error(error.message ?: "Unknown error")
                        }
                    }
            } catch (e: CancellationException) {
            } catch (e: IllegalStateException) {
            } catch (e: Exception) {
                if (coroutineContext.isActive) {
                    try {
                        _fixturesState.value = FixturesUiState.Error(e.message ?: "Unknown error")
                    } catch (ex: Exception) {
                    }
                }
            }
        }
    }
}

sealed class LeaguesUiState {
    object Idle : LeaguesUiState()
    object Loading : LeaguesUiState()
    data class Success(val leagues: List<League>) : LeaguesUiState()
    data class Error(val message: String) : LeaguesUiState()
}

sealed class TeamsUiState {
    object Idle : TeamsUiState()
    object Loading : TeamsUiState()
    data class Success(val teams: List<Team>) : TeamsUiState()
    data class Error(val message: String) : TeamsUiState()
}

sealed class FixturesUiState {
    object Idle : FixturesUiState()
    object Loading : FixturesUiState()
    data class Success(val fixtures: List<Fixture>) : FixturesUiState()
    data class Error(val message: String) : FixturesUiState()
}

