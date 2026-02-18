package com.example.goalpulse.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalpulse.config.AppConstants
import com.example.goalpulse.data.repository.TeamsRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamsViewModel(
    private val repository: TeamsRepository
) : ViewModel() {
    
    private val _teamsState = MutableStateFlow<TeamsUiState>(TeamsUiState.Idle)
    val teamsState: StateFlow<TeamsUiState> = _teamsState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private var searchTeamsJob: Job? = null
    private var loadPopularTeamsJob: Job? = null
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchTeams(query)
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
                delay(AppConstants.SEARCH_DEBOUNCE_DELAY_MS)
                ensureActive()
                _teamsState.value = TeamsUiState.Loading
                repository.searchTeams(query)
                    .onSuccess { teams ->
                        ensureActive()
                        _teamsState.value = TeamsUiState.Success(teams)
                    }
                    .onFailure { error ->
                        ensureActive()
                        _teamsState.value = TeamsUiState.Error(error.message ?: "Unknown error")
                    }
            } catch (e: CancellationException) {
            } catch (e: IllegalStateException) {
            } catch (e: Exception) {
                try {
                    ensureActive()
                    _teamsState.value = TeamsUiState.Error(e.message ?: "Unknown error")
                } catch (ex: Exception) {
                }
            }
        }
    }
    
    fun loadPopularTeams() {
        loadPopularTeamsJob?.cancel()
        searchTeamsJob?.cancel()
        loadPopularTeamsJob = viewModelScope.launch {
            try {
                ensureActive()
                _teamsState.value = TeamsUiState.Loading
                repository.getTeamsByLeague(AppConstants.PREMIER_LEAGUE_ID)
                    .onSuccess { teams ->
                        ensureActive()
                        _teamsState.value = TeamsUiState.Success(teams)
                    }
                    .onFailure { error ->
                        ensureActive()
                        _teamsState.value = TeamsUiState.Error(error.message ?: "Unknown error")
                    }
            } catch (e: CancellationException) {
            } catch (e: IllegalStateException) {
            } catch (e: Exception) {
                try {
                    ensureActive()
                    _teamsState.value = TeamsUiState.Error(e.message ?: "Unknown error")
                } catch (ex: Exception) {
                }
            }
        }
    }
}

