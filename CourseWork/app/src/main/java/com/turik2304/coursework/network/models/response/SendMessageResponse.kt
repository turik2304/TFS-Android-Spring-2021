package com.turik2304.coursework.network.models.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SendMessageResponse(
    @SerialName("id")
    val uidOfSentMessage: Int
)