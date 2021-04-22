package com.turik2304.coursework.network.models.response

import com.turik2304.coursework.network.models.data.ReactionEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetReactionEventResponse(
        @SerialName("events")
        val reactionEvents: List<ReactionEvent>
)

