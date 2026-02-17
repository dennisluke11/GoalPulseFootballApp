package com.example.goalpulse.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalpulse.data.repository.LeaguesRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LeaguesViewModel(
    private val repository: LeaguesRepository
) : ViewModel() {
    
    private val _leaguesState = MutableStateFlow<LeaguesUiState>(LeaguesUiState.Idle)
    val leaguesState: StateFlow<LeaguesUiState> = _leaguesState.asStateFlow()
    
    private var searchLeaguesJob: Job? = null
    
    init {
        loadAllLeagues()
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
                if (e !is IllegalStateException) {
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
                if (e !is IllegalStateException) {
                    try {
                        ensureActive()
                        _leaguesState.value = LeaguesUiState.Error(e.message ?: "Unknown error")
                    } catch (ce: CancellationException) {
                    }
                }
            }
        }
    }
}

