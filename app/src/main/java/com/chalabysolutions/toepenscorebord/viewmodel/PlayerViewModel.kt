package com.chalabysolutions.toepenscorebord.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.chalabysolutions.toepenscorebord.data.database.AppDatabase
import com.chalabysolutions.toepenscorebord.data.model.Player
import com.chalabysolutions.toepenscorebord.data.repository.ToepenRepository
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: ToepenRepository =
        ToepenRepository(AppDatabase.getDatabase(application))

    // LiveData lijst van spelers
    val players: LiveData<List<Player>> = repo.players.asLiveData()

    // Nieuwe speler toevoegen
    fun addPlayer(name: String) = viewModelScope.launch {
        repo.addPlayer(Player(name = name))
    }

    // Punten toevoegen (bijv. +1, +2 bij armoede, +1 extra bij kloppen)
    fun addPoints(player: Player, points: Int = 1) = viewModelScope.launch {
        val newPoints = player.points + points
        val updatedPlayer = player.copy(
            points = newPoints,
            eliminated = newPoints >= 15 // bij 15 punten is speler uit
        )
        repo.updatePlayer(updatedPlayer)
    }

    // Reset (gebruik bij nieuwe avond)
    fun resetPlayers() = viewModelScope.launch {
        repo.clearPlayers()
    }
}