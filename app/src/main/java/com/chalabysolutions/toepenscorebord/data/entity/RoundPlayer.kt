package com.chalabysolutions.toepenscorebord.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "round_player",
    foreignKeys = [
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Round::class,
            parentColumns = ["id"],
            childColumns = ["roundId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoundPlayer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roundId: Int, // FK naar Round
    val playerId: Int, // FK naar Player
    var points: Int = 0,
    var eliminated: Boolean = false
)