package com.example.goalpulse.ui.viewmodel

import com.example.goalpulse.data.model.League

sealed class LeaguesUiState {
    object Idle : LeaguesUiState()
    object Loading : LeaguesUiState()
    data class Success(val leagues: List<League>) : LeaguesUiState()
    data class Error(val message: String) : LeaguesUiState()
}

