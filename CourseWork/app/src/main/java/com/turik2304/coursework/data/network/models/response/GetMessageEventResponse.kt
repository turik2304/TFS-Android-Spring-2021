package com.turik2304.coursework.data.network.models.response

import com.turik2304.coursework.data.network.models.data.MessageEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetMessageEventResponse(
    @SerialName("events")
    val messageEvents: List<MessageEvent>
)

