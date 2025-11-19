package com.chalabysolutions.toepenscorebord.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.chalabysolutions.toepenscorebord.data.entity.Round
import com.chalabysolutions.toepenscorebord.data.relation.RoundWithPlayers
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundDao {

    @Query("DELETE FROM round")
    suspend fun deleteAllRounds()

    @Query("DELETE FROM round WHERE sessionId = :sessionId")
    suspend fun deleteRoundsBySessionId(sessionId: Int)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: Round): Long

    @Update
    suspend fun updateRound(round: Round)

    @Delete
    suspend fun deleteRound(round: Round)

    @Transaction
    @Query("SELECT * FROM round WHERE id = :roundId")
    fun getRoundWithPlayers(roundId: Int): Flow<RoundWithPlayers>

    @Transaction
    @Query("SELECT * FROM round")
    fun getRoundsWithPlayers(): Flow<List<RoundWithPlayers>>

    @Query("SELECT MAX(roundNumber) FROM round WHERE sessionId = :sessionId")
    suspend fun getMaxRoundNumber(sessionId: Int): Int?

    @Query("UPDATE round SET active = 0 WHERE sessionId = :sessionId AND active = 1")
    suspend fun deactivateAllRoundsInSession(sessionId: Int)
}