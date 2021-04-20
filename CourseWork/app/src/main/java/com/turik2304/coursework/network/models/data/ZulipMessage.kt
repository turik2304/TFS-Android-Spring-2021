package com.turik2304.coursework.network.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val uid: Int
)