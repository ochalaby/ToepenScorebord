package com.chalabysolutions.toepenscorebord.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chalabysolutions.toepenscorebord.data.entity.RoundPlayer

@Dao
interface RoundPlayerDao {

    @Query("DELETE FROM round_player")
    suspend fun deleteAllRoundPlayers()

    @Query("DELETE FROM round_player WHERE roundId  = :roundId")
    suspend fun deleteByRoundId(roundId: Int)

    @Query("DELETE FROM round_player WHERE roundId IN (SELECT id FROM round WHERE sessionId = :sessionId)")
    suspend fun deleteBySessionId(sessionId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(roundPlayer: RoundPlayer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(roundPlayers: List<RoundPlayer>)

    @Update
    suspend fun update(roundPlayer: RoundPlayer)

    @Delete
    suspend fun delete(roundPlayer: RoundPlayer)

    @Query("SELECT * FROM round_player WHERE roundId = :roundId AND playerId = :playerId LIMIT 1")
    suspend fun getRoundPlayer(roundId: Int, playerId: Int): RoundPlayer?

    @Query("SELECT * FROM round_player WHERE roundId = :roundId")
    suspend fun getRoundPlayers(roundId: Int): List<RoundPlayer>

    @Update
    suspend fun updateRoundPlayers(roundPlayers: List<RoundPlayer>)

    @Query("SELECT COUNT(*) FROM round_player WHERE playerId = :playerId")
    suspend fun countByPlayerId(playerId: Int): Int
}