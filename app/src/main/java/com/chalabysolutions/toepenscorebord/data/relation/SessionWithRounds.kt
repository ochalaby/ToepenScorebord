package com.chalabysolutions.toepenscorebord.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.chalabysolutions.toepenscorebord.data.entity.Session
import com.chalabysolutions.toepenscorebord.data.entity.Round

data class SessionWithRounds(
    @Embedded val session: Session,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val rounds: List<Round>
)