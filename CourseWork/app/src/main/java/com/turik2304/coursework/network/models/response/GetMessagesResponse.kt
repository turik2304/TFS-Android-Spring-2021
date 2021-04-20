package com.turik2304.coursework.network.models.response

import com.turik2304.coursework.network.models.data.ZulipMessage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetMessagesResponse(
    @SerialName("messages")
    val messages: List<ZulipMessage>
)

