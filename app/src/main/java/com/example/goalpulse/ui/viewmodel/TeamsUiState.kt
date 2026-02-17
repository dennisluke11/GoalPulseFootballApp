package com.example.goalpulse.ui.viewmodel

import com.example.goalpulse.data.model.Team

sealed class TeamsUiState {
    object Idle : TeamsUiState()
    object Loading : TeamsUiState()
    data class Success(val teams: List<Team>) : TeamsUiState()
    data class Error(val message: String) : TeamsUiState()
}

