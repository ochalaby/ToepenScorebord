package com.chalabysolutions.toepenscorebord.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.chalabysolutions.toepenscorebord.data.database.AppDatabase
import com.chalabysolutions.toepenscorebord.data.model.GameRound
import com.chalabysolutions.toepenscorebord.data.repository.ToepenRepository
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: ToepenRepository =
        ToepenRepository(AppDatabase.getDatabase(application))

    // Alle gespeelde rondes
    val rounds = repo.rounds.asLiveData()

    // Nieuwe ronde opslaan
    fun addRound(winnerId: Int, playerResults: String) = viewModelScope.launch {
        repo.addRound(
            GameRound(
                winnerId = winnerId,
                playerResults = playerResults
            )
        )
    }
}