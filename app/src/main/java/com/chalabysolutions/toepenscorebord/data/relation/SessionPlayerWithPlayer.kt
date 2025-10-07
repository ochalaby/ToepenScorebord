package com.chalabysolutions.toepenscorebord.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.data.entity.SessionPlayer

data class SessionPlayerWithPlayer(
    @Embedded val sessionPlayer: SessionPlayer,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "id"
    )
    val player: Player
)