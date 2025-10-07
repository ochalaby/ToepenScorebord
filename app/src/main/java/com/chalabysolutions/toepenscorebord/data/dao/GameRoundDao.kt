package com.chalabysolutions.toepenscorebord.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chalabysolutions.toepenscorebord.data.model.GameRound
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRoundDao {
    @Insert
    suspend fun insert(round: GameRound)

    @Query("SELECT * FROM rounds ORDER BY date DESC")
    fun getAllRounds(): Flow<List<GameRound>>
}