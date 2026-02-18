package com.example.goalpulse.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.goalpulse.data.model.Team
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TeamDetailViewModel : ViewModel() {
    
    private val _teamDetailState = MutableStateFlow<TeamDetailUiState>(TeamDetailUiState.Loading)
    val teamDetailState: StateFlow<TeamDetailUiState> = _teamDetailState.asStateFlow()
    
    fun loadTeam(teamJson: String) {
        try {
            val team = Gson().fromJson(teamJson, Team::class.java)
            if (team != null) {
                _teamDetailState.value = TeamDetailUiState.Success(team)
            } else {
                _teamDetailState.value = TeamDetailUiState.Error("Failed to parse team data")
            }
        } catch (e: Exception) {
            _teamDetailState.value = TeamDetailUiState.Error(e.message ?: "Unknown error")
        }
    }
}

