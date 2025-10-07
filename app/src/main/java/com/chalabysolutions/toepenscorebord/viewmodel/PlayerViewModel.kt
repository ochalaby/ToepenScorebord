package com.chalabysolutions.toepenscorebord.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.data.repository.ToepenRepository
import com.chalabysolutions.toepenscorebord.viewmodel.SessionViewModel.PlayerSelection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: ToepenRepository
) : ViewModel() {

    // Flow met alle actieve spelers (default voor SessionPlayersScreen)
    private val _allPlayers = MutableStateFlow<List<Player>>(emptyList())
    val allPlayers: StateFlow<List<Player>> = _allPlayers.asStateFlow()

    // Map van playerId -> kan verwijderd worden
    private val _canDeleteMap = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val canDeleteMap: StateFlow<Map<Int, Boolean>> = _canDeleteMap.asStateFlow()

    private val _showInactive = MutableStateFlow(false)
    val showInactive: StateFlow<Boolean> = _showInactive

    fun toggleShowInactive() {
        _showInactive.value = !_showInactive.value
    }

    init {
        // dynamisch alle spelers ophalen (actief of alles afhankelijk van toggle)
        viewModelScope.launch {
            combine(
                repository.getAllPlayers(), // alle spelers
                repository.getAllActivePlayers(), // alleen actieve spelers
                _showInactive
            ) { all, active, showInactive ->
                if (showInactive) all else active
            }.collect { players ->
                _allPlayers.value = players
                refreshCanDeleteStates(players)
            }
        }
    }

    // Maak een nieuwe speler aan in de database
    fun createNewPlayer(name: String) {
        viewModelScope.launch {
            repository.createPlayer(name)
        }
    }

    fun togglePlayerActive(playerId: Int) {
        viewModelScope.launch {
            val player = _allPlayers.value.find { it.id == playerId } ?: return@launch
            val newActive = !player.active
            repository.setPlayerActive(player.id, newActive)
            // Update lokale Flow
            _allPlayers.value = _allPlayers.value.map {
                if (it.id == playerId) it.copy(active = newActive) else it
            }
        }
    }

    /** Verwijdert een speler alleen als die nog niet gebruikt wordt in sessies/ronde */
    fun deletePlayerIfUnused(playerId: Int) {
        viewModelScope.launch {
            repository.deletePlayerIfUnused(playerId)
            _allPlayers.value = _allPlayers.value.filterNot { it.id == playerId }
            refreshCanDeleteStates(_allPlayers.value)
        }
    }

    // Refresh delete state
    private fun refreshCanDeleteStates(players: List<Player>) {
        viewModelScope.launch {
            val map = players.associate { player ->
                val canDelete = !repository.isPlayerUsed(player.id)
                player.id to canDelete
            }
            _canDeleteMap.value = map
        }
    }
}