package com.chalabysolutions.toepenscorebord.data.repository

import com.chalabysolutions.toepenscorebord.data.database.AppDatabase
import com.chalabysolutions.toepenscorebord.data.model.GameRound
import com.chalabysolutions.toepenscorebord.data.model.Player

class ToepenRepository(private val db: AppDatabase) {
    val players = db.playerDao().getAllPlayers()
    val rounds = db.gameRoundDao().getAllRounds()

    suspend fun addPlayer(player: Player) = db.playerDao().insert(player)
    suspend fun updatePlayer(player: Player) = db.playerDao().update(player)
    suspend fun clearPlayers() = db.playerDao().clear()

    suspend fun addRound(round: GameRound) = db.gameRoundDao().insert(round)
}