package com.example.goalpulse.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.goalpulse.data.model.Fixture
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FixtureDetailViewModel : ViewModel() {
    
    private val gson = Gson()
    private val _fixtureDetailState = MutableStateFlow<FixtureDetailUiState>(FixtureDetailUiState.Loading)
    val fixtureDetailState: StateFlow<FixtureDetailUiState> = _fixtureDetailState.asStateFlow()
    
    fun loadFixture(fixtureJson: String) {
        try {
            val fixture = gson.fromJson(fixtureJson, Fixture::class.java)
            if (fixture != null) {
                _fixtureDetailState.value = FixtureDetailUiState.Success(fixture)
            } else {
                _fixtureDetailState.value = FixtureDetailUiState.Error("Failed to parse fixture data")
            }
        } catch (e: Exception) {
            _fixtureDetailState.value = FixtureDetailUiState.Error(e.message ?: "Unknown error")
        }
    }
}

