package com.chalabysolutions.toepenscorebord.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rounds")
data class GameRound(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val winnerId: Int,
    val playerResults: String // JSON-string met punten per speler
)