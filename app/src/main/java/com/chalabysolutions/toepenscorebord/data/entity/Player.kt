package com.chalabysolutions.toepenscorebord.data.entity

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "player")
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val active: Boolean = true // Doet niet meer mee aan speel avonden, maar nog wel beschikbaar voor historische gegevens
)