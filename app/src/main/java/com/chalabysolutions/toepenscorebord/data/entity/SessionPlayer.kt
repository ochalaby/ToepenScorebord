package com.chalabysolutions.toepenscorebord.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "session_player",
    primaryKeys = ["sessionId", "playerId"],
    foreignKeys = [
        ForeignKey(
            entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("playerId")]
)
data class SessionPlayer(
    val sessionId: Int,
    val playerId: Int,
    val active: Boolean = true // geeft aan of speler nog actief is in de sessie
)