package com.chalabysolutions.toepenscorebord.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chalabysolutions.toepenscorebord.data.entity.Player
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Query("SELECT * FROM player WHERE id = :playerId LIMIT 1")
    suspend fun getPlayerById(playerId: Int): Player?

    // Voor Flow (optioneel, als je realtime updates wilt)
    @Query("SELECT * FROM player WHERE id = :playerId LIMIT 1")
    fun getPlayerByIdFlow(playerId: Int): Flow<Player?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player): Long

    @Update
    suspend fun updatePlayer(player: Player)

    @Delete
    suspend fun deletePlayer(player: Player)

    // Alle spelers ophalen
    @Query("SELECT * FROM player ORDER BY name ASC")
    fun getAllPlayers(): Flow<List<Player>>

    // Alleen actieve spelers
    @Query("SELECT * FROM player WHERE active = 1 ORDER BY name ASC")
    fun getActivePlayers(): Flow<List<Player>>

    @Query("UPDATE player SET active = :active WHERE id = :playerId")
    suspend fun updateActiveStatus(playerId: Int, active: Boolean)
}