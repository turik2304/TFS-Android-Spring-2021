package com.turik2304.coursework.data.network.models.data

import com.turik2304.coursework.data.network.models.RemoteModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("reactions")
class Reaction(
    @SerialName("emoji_code")
    val emojiCode: String,
    @SerialName("user_id")
    var userId: Int,
)