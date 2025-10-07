package com.chalabysolutions.toepenscorebord.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.data.relation.SessionWithRounds
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Transaction
    @Query("SELECT * FROM session WHERE id = :sessionId LIMIT 1")
    fun getSessionWithRoundsFlow(sessionId: Int): Flow<SessionWithRounds>

    // wat je al had:
    @Query("SELECT * FROM session ORDER BY date DESC")
    fun getAllSessionsFlow(): Flow<List<Session>>

    @Query("SELECT * FROM session WHERE active = 1 LIMIT 1")
    fun getActiveSessionFlow(): Flow<Session?>

    @Query("SELECT * FROM session WHERE active = 1 LIMIT 1")
    suspend fun getActiveSessionOnce(): Session?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    @Update
    suspend fun updateSession(session: Session)

    @Query("UPDATE session SET active = 0")
    suspend fun deactivateAllSessions()
}