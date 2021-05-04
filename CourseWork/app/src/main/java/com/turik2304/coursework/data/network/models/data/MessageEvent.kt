package com.turik2304.coursework.data.network.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MessageEvent(
    @SerialName("message")
    val message: Message,
    @SerialName("id")
    val id: String
)