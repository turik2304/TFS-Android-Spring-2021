package com.turik2304.coursework.data.network.models.response

import com.turik2304.coursework.data.network.models.data.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetMessagesResponse(
    @SerialName("messages")
    val messages: List<Message>
)