package com.chalabysolutions.toepenscorebord.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalabysolutions.toepenscorebord.data.entity.Round
import com.chalabysolutions.toepenscorebord.data.relation.RoundWithPlayers
import com.chalabysolutions.toepenscorebord.data.repository.ToepenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoundViewModel @Inject constructor(
    private val repository: ToepenRepository
) : ViewModel() {

    data class UiState(
        val roundWithPlayers: RoundWithPlayers? = null,
        val isLoading: Boolean = true
    )
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    var knockCounter = mutableStateOf(0)
        private set

    fun loadRound(roundId: Int) {
        viewModelScope.launch {
            repository.getRoundWithPlayers(roundId).collect { roundWithPlayers ->
                _uiState.value = UiState(roundWithPlayers)
            }
        }
    }

    fun pass(roundId: Int, playerId: Int) {
        viewModelScope.launch {
            repository.updateRoundPlayer(
                roundId = roundId,
                playerId = playerId,
                update = { current ->
                    val extraPoints = if (knockCounter.value == 0) 1 else knockCounter.value
                    current.copy(
                        points = current.points + extraPoints,
                        eliminated = true
                    )
                }
            )
        }
    }

    fun knock() {
        viewModelScope.launch {
            knockCounter.value += 1
        }
    }

    fun win(roundId: Int, winnerPlayerId: Int) {
        viewModelScope.launch {
            val extraPoints = 1 + knockCounter.value
            val roundWithPlayers = repository.getRoundWithPlayers(roundId).first() // <---
            roundWithPlayers.players.forEach { pr ->
                if (pr.player.id != winnerPlayerId && !pr.roundPlayer.eliminated) {
                    repository.updateRoundPlayer(roundId, pr.player.id) { current ->
                        current.copy(
                            points = current.points + extraPoints,
                            eliminated = true
                        )
                    }
                } else if (pr.player.id == winnerPlayerId) {
                    repository.updateRoundPlayer(roundId, pr.player.id) { current ->
                        current.copy(eliminated = true) // winnaar ook uitschakelen
                    }
                }
            }
        }
    }

    fun endCurrentGame(round: Round) {
        viewModelScope.launch {
            repository.resetPlayerEliminationStatus(round.id)
            knockCounter.value = 0
        }
    }
}