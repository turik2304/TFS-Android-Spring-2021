package com.turik2304.coursework.network.calls

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetMessagesResponse(
    @SerialName("messages")
    val messages: List<Message>
)

@Serializable
data class Message(
    @SerialName("sender_full_name")
    val userName: String,
    @SerialName("content")
    val message: String,
    @SerialName("timestamp")
    val dateInSeconds: Int,
    @SerialName("sender_id")
    val userId: String,
    val reactions: List<Reaction>,
    @SerialName("id")
    val uid: String
)

@Serializable
@SerialName("reactions")
data class Reaction(
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("user_id")
    var userId: Int,
)
