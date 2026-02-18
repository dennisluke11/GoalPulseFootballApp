package com.example.goalpulse.ui.viewmodel

import com.example.goalpulse.data.model.Fixture

sealed class FixtureDetailUiState {
    object Loading : FixtureDetailUiState()
    data class Success(val fixture: Fixture) : FixtureDetailUiState()
    data class Error(val message: String) : FixtureDetailUiState()
}

