package com.chalabysolutions.toepenscorebord.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "round")
data class Round(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sessionId: Int, // FK naar CardSession
    val maxPoints: Int = 15, // kan 10 of 15 zijn
    val active: Boolean = true,
    var winnerId: Int? = null,
    var currentGameId: Int? = null
)