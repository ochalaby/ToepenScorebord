package com.chalabysolutions.toepenscorebord.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.chalabysolutions.toepenscorebord.data.entity.Player
import com.chalabysolutions.toepenscorebord.data.entity.RoundPlayer

data class RoundPlayerWithPlayer(
    @Embedded val roundPlayer: RoundPlayer,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "id"
    )
    val player: Player
)