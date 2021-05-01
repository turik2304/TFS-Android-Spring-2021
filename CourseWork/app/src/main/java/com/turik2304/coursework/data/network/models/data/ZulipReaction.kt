package com.turik2304.coursework.data.network.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("reactions")
data class ZulipReaction(
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("user_id")
    var userId: Int,
)