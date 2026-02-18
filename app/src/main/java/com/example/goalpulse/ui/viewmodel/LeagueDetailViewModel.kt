package com.example.goalpulse.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.goalpulse.data.model.League
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LeagueDetailViewModel : ViewModel() {
    
    private val gson = Gson()
    private val _leagueDetailState = MutableStateFlow<LeagueDetailUiState>(LeagueDetailUiState.Loading)
    val leagueDetailState: StateFlow<LeagueDetailUiState> = _leagueDetailState.asStateFlow()
    
    fun loadLeague(leagueJson: String) {
        try {
            val league = gson.fromJson(leagueJson, League::class.java)
            if (league != null) {
                _leagueDetailState.value = LeagueDetailUiState.Success(league)
            } else {
                _leagueDetailState.value = LeagueDetailUiState.Error("Failed to parse league data")
            }
        } catch (e: Exception) {
            _leagueDetailState.value = LeagueDetailUiState.Error(e.message ?: "Unknown error")
        }
    }
}

