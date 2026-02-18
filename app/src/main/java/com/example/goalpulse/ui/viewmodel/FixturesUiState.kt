package com.example.goalpulse.ui.viewmodel

import com.example.goalpulse.data.model.Fixture

sealed class FixturesUiState {
    object Idle : FixturesUiState()
    object Loading : FixturesUiState()
    data class Success(val fixtures: List<Fixture>) : FixturesUiState()
    data class Error(val message: String) : FixturesUiState()
}



