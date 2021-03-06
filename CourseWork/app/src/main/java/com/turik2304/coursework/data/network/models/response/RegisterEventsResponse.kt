package com.turik2304.coursework.data.network.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RegisterEventsResponse(
    @SerialName("queue_id")
    val queueId: String,
    @SerialName("last_event_id")
    val lastEventId: String
)

