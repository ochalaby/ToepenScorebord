package com.chalabysolutions.toepenscorebord.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.data.entity.Round
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.data.repository.ToepenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val repository: ToepenRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Int = savedStateHandle.get<String>("sessionId")?.toIntOrNull()
    ?: throw IllegalArgumentException("sessionId argument ontbreekt of is geen getal")

    data class UiState(
        val session: Session? = null,
        val players: List<PlayerSelection> = emptyList(),
        val rounds: List<Round> = emptyList(),
        val isLoading: Boolean = true
    )

    data class PlayerSelection(
        val player: Player,
        val isActive: Boolean
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getSessionWithRounds(sessionId),
                repository.getPlayersForSession(sessionId)
            ) { sessionWithRounds, sessionPlayers ->
                UiState(
                    session = sessionWithRounds.session,
                    rounds = sessionWithRounds.rounds,
                    players = sessionPlayers.map { sp ->
                        PlayerSelection(player = sp.player, isActive = sp.sessionPlayer.active)
                    },
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    // Toggle actief/niet-actief voor een speler
    fun togglePlayerActiveInSession(playerId: Int) {
        viewModelScope.launch {
            val current = _uiState.value.players
            val updatedPlayers = current.map {
                if (it.player.id == playerId) {
                    val newActive = !it.isActive
                    repository.setPlayerActiveInSession(sessionId, playerId, newActive)
                    it.copy(isActive = newActive)
                } else it
            }
            _uiState.value = _uiState.value.copy(players = updatedPlayers)
        }
    }

    // Voeg één speler toe aan de sessie en update lokale state
    private fun addPlayerToSession(player: Player) {
        viewModelScope.launch {
            repository.addPlayerToSession(sessionId, player.id)
            val currentPlayers = _uiState.value.players
            _uiState.value = _uiState.value.copy(
                players = currentPlayers + PlayerSelection(player, isActive = true)
            )
        }
    }

    // Voeg meerdere spelers toe (multi-select)
    fun addPlayersToSession(playerIds: List<Int>) {
        viewModelScope.launch {
            playerIds.forEach { pid ->
                repository.getPlayer(pid)?.let { player ->
                    addPlayerToSession(player)
                }
            }
        }
    }

    // Start een nieuwe ronde met alle actieve spelers
    fun startNewRoundAndNavigate(onCreated: (Int) -> Unit) {
        viewModelScope.launch {
            val newId = repository.startNewRound(sessionId, maxPoints = 15)
            onCreated(newId)
        }
    }

    fun deleteRound(round: Round) {
        viewModelScope.launch {
            repository.deleteRound(round)
        }
    }
}