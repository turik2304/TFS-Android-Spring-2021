package com.turik2304.coursework.network.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageEvent(
    @SerialName("message")
    val message: ZulipMessage,
    @SerialName("id")
    val id: String
)