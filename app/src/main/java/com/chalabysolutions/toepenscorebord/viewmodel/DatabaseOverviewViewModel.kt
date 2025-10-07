package com.chalabysolutions.toepenscorebord.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.data.relation.RoundWithPlayers
import com.chalabysolutions.toepenscorebord.data.relation.SessionWithPlayers
import com.chalabysolutions.toepenscorebord.data.relation.SessionWithRounds
import com.chalabysolutions.toepenscorebord.data.repository.ToepenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DatabaseOverviewViewModel @Inject constructor(
    private val repository: ToepenRepository
) : ViewModel() {

    data class UiState(
        val sessionsWithRounds: List<SessionWithRounds> = emptyList(),
        val sessionsWithPlayers: List<SessionWithPlayers> = emptyList(),
        val roundsWithPlayers: List<RoundWithPlayers> = emptyList(),
        val isLoading: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState(isLoading = true))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadDatabaseOverview()
    }

    fun loadDatabaseOverview() {
        viewModelScope.launch {
            // Sessions + Rounds
            repository.getSessionsWithRounds().collect { sessionsWithRounds ->
                _uiState.value = _uiState.value.copy(
                    sessionsWithRounds = sessionsWithRounds,
                    isLoading = false
                )
            }
        }
        viewModelScope.launch {
            // Sessions + Players
            repository.getSessionsWithPlayers().collect { sessionsWithPlayers ->
                _uiState.value = _uiState.value.copy(
                    sessionsWithPlayers = sessionsWithPlayers,
                    isLoading = false
                )
            }
        }
        viewModelScope.launch {
            // Rounds + Players
            repository.getRoundsWithPlayers().collect { roundsWithPlayers ->
                _uiState.value = _uiState.value.copy(
                    roundsWithPlayers = roundsWithPlayers,
                    isLoading = false
                )
            }
        }
    }
}