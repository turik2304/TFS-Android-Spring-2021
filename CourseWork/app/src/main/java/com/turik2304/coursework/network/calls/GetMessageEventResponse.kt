package com.turik2304.coursework.network.calls

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetMessageEventResponse(
        @SerialName("events")
        val events: List<Event>
)

@Serializable
data class Event(
        @SerialName("message")
        val message: ZulipMessage,
        @SerialName("id")
        val id: String
)