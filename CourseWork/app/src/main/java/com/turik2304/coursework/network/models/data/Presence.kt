package com.turik2304.coursework.network.models.data

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
enum class StatusEnum(val status: String) {
    @SerialName("active")
    ACTIVE("active"),

    @SerialName("idle")
    IDLE("idle"),

    @SerialName("offline")
    OFFLINE("offline")
}