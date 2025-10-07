package com.chalabysolutions.toepenscorebord.data.repository

import com.chalabysolutions.toepenscorebord.data.database.AppDatabase
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.data.entity.RoundPlayer
import com.chalabysolutions.toepenscorebord.data.entity.Round
import com.chalabysolutions.toepenscorebord.data.entity.SessionPlayer
import com.chalabysolutions.toepenscorebord.data.relation.RoundWithPlayers
import com.chalabysolutions.toepenscorebord.data.relation.SessionPlayerWithPlayer
import com.chalabysolutions.toepenscorebord.data.relation.SessionWithPlayers
import com.chalabysolutions.toepenscorebord.data.relation.SessionWithRounds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ToepenRepository(private val db: AppDatabase) {

    suspend fun clearDatabase() = withContext(Dispatchers.IO) {
        db.clearAllTables()
    }

    suspend fun clearDatabaseExceptPlayers() {
        db.roundPlayerDao().deleteAllRoundPlayers()
        db.sessionPlayerDao().deleteAllSessionPlayers()
        db.roundDao().deleteAllRounds()
        db.sessionDao().deleteAllSessions()
    }

    // ============================
    // Players
    // ============================
    fun getAllPlayers(): Flow<List<Player>> = db.playerDao().getAllPlayers()
    fun getAllActivePlayers(): Flow<List<Player>> = db.playerDao().getActivePlayers()

    suspend fun getPlayer(playerId: Int): Player? = db.playerDao().getPlayerById(playerId)

    // Maak nieuwe speler
    suspend fun createPlayer(name: String) {
        val player = Player(name = name, active = true)
        db.playerDao().insertPlayer(player)
    }

    suspend fun setPlayerActive(playerId: Int, active: Boolean) {
        db.playerDao().updateActiveStatus(playerId, active)
    }

    suspend fun isPlayerUsed(playerId: Int): Boolean {
        val countInSessions = db.sessionPlayerDao().countByPlayerId(playerId)
        val countInRounds = db.roundPlayerDao().countByPlayerId(playerId)
        return countInSessions > 0 || countInRounds > 0
    }

    // Verwijder alleen als niet in gebruik
    suspend fun deletePlayerIfUnused(playerId: Int) {
        if (!isPlayerUsed(playerId)) {
            val player: Player? = db.playerDao().getPlayerById(playerId)
            player?.let {db.playerDao().deletePlayer(it)}
        }
    }

    // ============================
    // SessionPlayers
    // ============================
    fun getPlayersForSession(sessionId: Int): Flow<List<SessionPlayerWithPlayer>> =
        db.sessionPlayerDao().getSessionPlayers(sessionId)

    suspend fun addPlayerToSession(sessionId: Int, playerId: Int) {
        db.sessionPlayerDao().insert(SessionPlayer(sessionId = sessionId, playerId = playerId, active = true))
    }

    suspend fun setPlayerActiveInSession(sessionId: Int, playerId: Int, active: Boolean) {
        db.sessionPlayerDao().updateActiveStatus(sessionId, playerId, active)
    }

    // ============================
    // RoundPlayer
    // ============================

    suspend fun updateRoundPlayer(
        roundId: Int,
        playerId: Int,
        update: (RoundPlayer) -> RoundPlayer
    ) {
        val roundPlayer = db.roundPlayerDao().getRoundPlayer(roundId, playerId)
        roundPlayer?.let {
            val updatedRoundPlayer = update(it)
            db.roundPlayerDao().update(updatedRoundPlayer)
        }
    }

    // ============================
    // Sessions
    // ============================
    val allSessions: Flow<List<Session>> = db.sessionDao().getAllSessions()

    fun getSessionWithRounds(sessionId: Int): Flow<SessionWithRounds> =
        db.sessionDao().getSessionWithRounds(sessionId)

    fun getSessionsWithRounds(): Flow<List<SessionWithRounds>> =
        db.sessionDao().getSessionsWithRounds()

    fun getSessionsWithPlayers(): Flow<List<SessionWithPlayers>> =
        db.sessionDao().getSessionsWithPlayers()

    suspend fun insertSession(): Int {
        // ️Sluit eventuele actieve sessie
        db.sessionDao().deactivateAllSessions()

        // Maak nieuwe sessie
        val newSession = Session(
            date = System.currentTimeMillis(),
            active = true
        )
        val sessionId = db.sessionDao().insertSession(newSession).toInt()
        return sessionId
    }

    // ============================
    // Rounds
    // ============================
    fun getRoundWithPlayers(roundId: Int): Flow<RoundWithPlayers> {
        return db.roundDao().getRoundWithPlayers(roundId)
    }

    fun getRoundsWithPlayers(): Flow<List<RoundWithPlayers>> =
        db.roundDao().getRoundsWithPlayers()

    suspend fun updateRound(round: Round) {
        db.roundDao().updateRound(round)
    }

    suspend fun resetPlayerEliminationStatus(roundId: Int) {
        val roundWithPlayers = getRoundWithPlayers(roundId).first() // haal snapshot
        roundWithPlayers.players.forEach { pr ->
            val resetEliminated = pr.roundPlayer.points >= roundWithPlayers.round.maxPoints // wie meer punten heeft blijft eliminated
            updateRoundPlayer(roundId, pr.player.id) { current ->
                current.copy(eliminated = resetEliminated)
            }
        }
    }

    suspend fun startNewRound(sessionId: Int, maxPoints: Int = 15): Int {
        // zet alle huidige rondes inactief
        db.roundDao().deactivateAllRoundsInSession(sessionId)

        val currentMax = db.roundDao().getMaxRoundNumber(sessionId) ?: 0

        // voeg nieuwe ronde toe
        val roundId = db.roundDao().insertRound(
            Round(
                sessionId = sessionId,
                roundNumber = currentMax + 1,
                maxPoints = maxPoints,
                active = true
            )
        ).toInt()

        val activePlayers = db.sessionPlayerDao().getActiveSessionPlayers(sessionId).first()
        val roundPlayers = activePlayers.map { sp ->
            RoundPlayer(roundId = roundId, playerId = sp.player.id)
        }
        db.roundPlayerDao().insertAll(roundPlayers)
        return roundId
    }
}