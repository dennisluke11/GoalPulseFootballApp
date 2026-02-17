package com.example.goalpulse.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goalpulse.data.repository.FixturesRepository
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FixturesViewModel(
    private val repository: FixturesRepository
) : ViewModel() {
    
    private val _fixturesState = MutableStateFlow<FixturesUiState>(FixturesUiState.Idle)
    val fixturesState: StateFlow<FixturesUiState> = _fixturesState.asStateFlow()
    
    private var loadFixturesJob: Job? = null
    
    fun loadFixtures(leagueId: Int? = null, season: Int? = null, teamId: Int? = null, date: String? = null) {
        loadFixturesJob?.cancel()
        loadFixturesJob = viewModelScope.launch {
            try {
                ensureActive()
                _fixturesState.value = FixturesUiState.Loading
                repository.getFixtures(leagueId, season, teamId, date)
                    .onSuccess { fixtures ->
                        ensureActive()
                        _fixturesState.value = FixturesUiState.Success(fixtures)
                    }
                    .onFailure { error ->
                        ensureActive()
                        _fixturesState.value = FixturesUiState.Error(error.message ?: "Unknown error")
                    }
            } catch (e: CancellationException) {
            } catch (e: IllegalStateException) {
            } catch (e: Exception) {
                if (e !is CancellationException && e !is IllegalStateException) {
                    try {
                        ensureActive()
                        _fixturesState.value = FixturesUiState.Error(e.message ?: "Unknown error")
                    } catch (ce: CancellationException) {
                    }
                }
            }
        }
    }
}

