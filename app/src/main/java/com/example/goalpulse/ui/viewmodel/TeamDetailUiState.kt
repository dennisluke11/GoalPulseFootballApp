package com.example.goalpulse.ui.viewmodel

import com.example.goalpulse.data.model.Team

sealed class TeamDetailUiState {
    object Loading : TeamDetailUiState()
    data class Success(val team: Team) : TeamDetailUiState()
    data class Error(val message: String) : TeamDetailUiState()
}

