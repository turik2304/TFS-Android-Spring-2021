package com.turik2304.coursework.network.models.response

import com.turik2304.coursework.network.models.data.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetMessageEventResponse(
        @SerialName("events")
        val events: List<Event>
)

