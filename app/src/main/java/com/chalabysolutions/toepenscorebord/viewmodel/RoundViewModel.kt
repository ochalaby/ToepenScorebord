package com.chalabysolutions.toepenscorebord.viewmodel

import androidx.compose.runtime.mutableIntStateOf
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

    var knockCounter = mutableIntStateOf(0)
        private set

    fun loadRound(roundId: Int) {
        viewModelScope.launch {
            repository.getRoundWithPlayers(roundId).collect { roundWithPlayers ->
                _uiState.value = UiState(roundWithPlayers)
            }
        }
    }

    fun toggleShortRound() {
        val currentRound = _uiState.value.roundWithPlayers?.round ?: return
        val newMaxPoints = if (currentRound.maxPoints == 15) 10 else 15
        val updatedRound = currentRound.copy(maxPoints = newMaxPoints)

        viewModelScope.launch {
            repository.updateRound(updatedRound) // schrijf naar DB
            val current = _uiState.value
            _uiState.value = current.copy(
                roundWithPlayers = current.roundWithPlayers?.copy(round = updatedRound)
            )
        }
    }

    fun win(roundId: Int, winnerPlayerId: Int) {
        viewModelScope.launch {
            val roundWithPlayers = repository.getRoundWithPlayers(roundId).first()
            val round = roundWithPlayers.round
            val isArmoede = roundWithPlayers.players.any { playerWithRound ->
                playerWithRound.roundPlayer.points == round.maxPoints - 1
            }

            // Geef de verliezers punten
            roundWithPlayers.players.forEach { pr ->
                val current = pr.roundPlayer
                var newPoints = current.points

                if (current.playerId != winnerPlayerId && !current.eliminated && current.points < round.maxPoints) {
                    // verliezers
                    val basePoints = 1 + knockCounter.intValue
                    val extraForArmoede = if (isArmoede) 1 else 0
                    newPoints = (current.points + basePoints + extraForArmoede)
                        .coerceAtMost(round.maxPoints)
                }
                repository.updateRoundPlayer(roundId, pr.player.id) {
                    it.copy(points = newPoints, eliminated = true)
                }
            }

            // Bepaal of er nu een winnaar is (enige speler < maxPoints)
            val updatedRoundWithPlayers  = repository.getRoundWithPlayers(roundId).first()
            val survivors = updatedRoundWithPlayers .players.filter { it.roundPlayer.points < updatedRoundWithPlayers .round.maxPoints }

            if (survivors.size == 1) {
                val winner = survivors.first()
                // Update round: winnaar + inactive
                repository.updateRound(updatedRoundWithPlayers .round.copy(
                    winnerId = winner.player.id,
                    active = false
                ))
                // Alle spelers inactief maken (al gebeurd via eliminated = true, maar voor zekerheid)
                updatedRoundWithPlayers.players.forEach { pr ->
                    repository.updateRoundPlayer(roundId, pr.player.id) {
                        it.copy(eliminated = true)
                    }
                }
            } else {
                // Geen winner, ronde nog actief?
                repository.updateRound(updatedRoundWithPlayers.round.copy(
                    active = false
                ))
            }

            // Reset klop-counter
            knockCounter.intValue = 0
        }
    }

    fun pass(roundId: Int, playerId: Int) {
        val extraPoints = if (knockCounter.intValue == 0) 1 else knockCounter.intValue
        addPoints(roundId, playerId, extraPoints)
    }

    fun addPoint(roundId: Int, playerId: Int) {
        addPoints(roundId, playerId, 1)
    }

    fun removePoint(roundId: Int, playerId: Int) {
        addPoints(roundId, playerId, -1)
    }

    private fun addPoints(roundId: Int, playerId: Int, points: Int) {
        viewModelScope.launch {
            repository.updateRoundPlayer(
                roundId = roundId,
                playerId = playerId,
                update = { current ->
                    current.copy(
                        points = current.points + points
                    )
                }
            )
        }
    }

    fun knock() {
        viewModelScope.launch {
            knockCounter.intValue += 1
        }
    }

    fun knockDown() {
        viewModelScope.launch {
            if (knockCounter.intValue > 0) knockCounter.intValue -= 1
        }
    }

    fun startNewGame(round: Round) {
        viewModelScope.launch {
            // Ronde tijdelijk inactief maken, tot er weer een nieuwe game gestart wordt
            val updated = repository.getRoundWithPlayers(round.id).first()
            repository.updateRound(updated.round.copy(
                active = true
            ))

            repository.resetPlayerEliminationStatus(round.id)
            knockCounter.intValue = 0
        }
    }

}