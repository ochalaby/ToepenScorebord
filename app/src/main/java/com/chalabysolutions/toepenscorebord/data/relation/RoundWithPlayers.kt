package com.chalabysolutions.toepenscorebord.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.chalabysolutions.toepenscorebord.data.entity.RoundPlayer
import com.chalabysolutions.toepenscorebord.data.entity.Round

data class RoundWithPlayers(
    @Embedded val round: Round,
    @Relation(
        parentColumn = "id",
        entityColumn = "roundId",
        entity = RoundPlayer::class
    )
    val players: List<RoundPlayerWithPlayer>
)