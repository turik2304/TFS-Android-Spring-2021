package com.turik2304.coursework.data.network.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionEvent(
    @SerialName("id")
    val id: String,
    @SerialName("op")
    val operation: OperationEnum,
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("message_id")
    val messageId: Int,
    @SerialName("user_id")
    val userId: Int,
)

@Serializable
enum class OperationEnum(val operation: String) {
    @SerialName("add")
    ADD("add"),

    @SerialName("remove")
    REMOVE("remove")
}
