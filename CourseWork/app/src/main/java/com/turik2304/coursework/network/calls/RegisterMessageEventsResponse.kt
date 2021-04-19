package com.turik2304.coursework.network.calls

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RegisterMessageEventsResponse(
    @SerialName("queue_id")
    val queueId: String,
    @SerialName("last_event_id")
    val lastEventId: String
)

