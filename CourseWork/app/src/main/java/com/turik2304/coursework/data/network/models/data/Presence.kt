package com.turik2304.coursework.data.network.models.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("presence")
class Presence(
    val aggregated: Aggregated
)

@Serializable
@SerialName("aggregated")
class Aggregated(
    @SerialName("status")
    val statusEnum: StatusEnum
)

@Serializable
enum class StatusEnum {
    @SerialName("active")
    ACTIVE,

    @SerialName("idle")
    IDLE,

    @SerialName("offline")
    OFFLINE
}