package com.example.goalpulse.ui.viewmodel

import com.example.goalpulse.data.model.League

sealed class LeagueDetailUiState {
    object Loading : LeagueDetailUiState()
    data class Success(val league: League) : LeagueDetailUiState()
    data class Error(val message: String) : LeagueDetailUiState()
}

