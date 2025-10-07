package com.chalabysolutions.toepenscorebord.data.model

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "players")
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    var points: Int = 0,
    var eliminated: Boolean = false
)