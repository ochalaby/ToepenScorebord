package com.chalabysolutions.toepenscorebord.data.dao

import androidx.lifecycle.LiveData
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: Round): Long

    @Update
    suspend fun updateRound(round: Round)

    @Delete
    suspend fun deleteRound(round: Round)

    @Query("SELECT * FROM round WHERE active = 1 LIMIT 1")
    fun getActiveRound(): LiveData<Round?>

    @Query("SELECT * FROM round WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getRoundsForSession(sessionId: Int): LiveData<List<Round>>

    @Query("SELECT * FROM round WHERE id = :roundId")
    fun getRound(roundId: Int): LiveData<Round>

    @Transaction
    @Query("SELECT * FROM round WHERE id = :roundId")
    fun getRoundWithPlayers(roundId: Int): LiveData<RoundWithPlayers>

    @Transaction
    @Query("SELECT * FROM round WHERE id = :roundId")
    fun getRoundWithPlayersFlow(roundId: Int): Flow<RoundWithPlayers>

    @Query("UPDATE round SET active = 0 WHERE sessionId = :sessionId AND active = 1")
    suspend fun deactivateAllRoundsInSession(sessionId: Int)
}