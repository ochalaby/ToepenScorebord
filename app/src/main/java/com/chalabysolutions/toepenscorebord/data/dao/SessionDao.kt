package com.chalabysolutions.toepenscorebord.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.data.relation.SessionWithPlayers
import com.chalabysolutions.toepenscorebord.data.relation.SessionWithRounds
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Query("DELETE FROM session")
    suspend fun deleteAllSessions()

    @Delete
    suspend fun deleteSession(session: Session)

    @Transaction
    @Query("SELECT * FROM session WHERE id = :sessionId LIMIT 1")
    fun getSessionWithRounds(sessionId: Int): Flow<SessionWithRounds>

    @Transaction
    @Query("SELECT * FROM session")
    fun getSessionsWithRounds(): Flow<List<SessionWithRounds>>

    @Transaction
    @Query("SELECT * FROM session")
    fun getSessionsWithPlayers(): Flow<List<SessionWithPlayers>>

    // wat je al had:
    @Query("SELECT * FROM session ORDER BY date DESC")
    fun getAllSessions(): Flow<List<Session>>

    @Query("SELECT * FROM session WHERE active = 1 LIMIT 1")
    fun getActiveSession(): Flow<Session?>

    @Query("SELECT * FROM session WHERE active = 1 LIMIT 1")
    suspend fun getActiveSessionOnce(): Session?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    @Update
    suspend fun updateSession(session: Session)

    @Query("UPDATE session SET active = 0")
    suspend fun deactivateAllSessions()
}