package com.turik2304.coursework.network.calls

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetMessagesResponse(
    @SerialName("messages")
    val messages: List<ZulipMessage>
)

@Serializable
data class ZulipMessage(
    @SerialName("sender_full_name")
    val userName: String,
    @SerialName("content")
    val message: String,
    @SerialName("timestamp")
    val dateInSeconds: Int,
    @SerialName("sender_id")
    val userId: Int,
    var reactions: List<ZulipReaction>,
    @SerialName("id")
    val uid: Int,
    @SerialName("avatar_url")
    val avatarUrl: String
)

@Serializable
@SerialName("reactions")
data class ZulipReaction(
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("user_id")
    var userId: Int,
)
