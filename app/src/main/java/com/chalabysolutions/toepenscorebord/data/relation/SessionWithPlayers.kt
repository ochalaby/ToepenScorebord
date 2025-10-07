package com.chalabysolutions.toepenscorebord.data.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.data.entity.SessionPlayer

data class SessionWithPlayers(
    @Embedded val session: Session,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = SessionPlayer::class,
            parentColumn = "sessionId",
            entityColumn = "playerId"
        )
    )
    val players: List<Player>
)