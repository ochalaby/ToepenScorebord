package com.chalabysolutions.toepenscorebord.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.data.entity.SessionPlayer
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionPlayerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sessionPlayer: SessionPlayer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sessionPlayers: List<SessionPlayer>)

    @Update
    suspend fun update(sessionPlayer: SessionPlayer)

    @Query("UPDATE session_player SET active = :active WHERE sessionId = :sessionId AND playerId = :playerId")
    suspend fun updateActiveStatus(sessionId: Int, playerId: Int, active: Boolean)

    // Haal Player-objecten die bij een session horen (gebruik JOIN)
    @Transaction
    @Query("""
        SELECT p.* FROM player p
        INNER JOIN session_player sp ON p.id = sp.playerId
        WHERE sp.sessionId = :sessionId
        ORDER BY p.name ASC
    """)
    fun getPlayersForSession(sessionId: Int): Flow<List<Player>>

    @Query("""
        SELECT p.* FROM player p
        INNER JOIN session_player sp ON p.id = sp.playerId
        WHERE sp.sessionId = :sessionId AND sp.active = 1
    """)
    fun getActivePlayers(sessionId: Int): Flow<List<Player>>

    @Delete
    suspend fun delete(sessionPlayer: SessionPlayer)

    @Query("DELETE FROM session_player WHERE sessionId = :sessionId")
    suspend fun deletePlayersForSession(sessionId: Int)

    @Query("DELETE FROM session_player WHERE sessionId = :sessionId AND playerId = :playerId")
    suspend fun removePlayerFromSession(sessionId: Int, playerId: Int)

    @Query("SELECT COUNT(*) FROM session_player WHERE playerId = :playerId")
    suspend fun countByPlayerId(playerId: Int): Int
}