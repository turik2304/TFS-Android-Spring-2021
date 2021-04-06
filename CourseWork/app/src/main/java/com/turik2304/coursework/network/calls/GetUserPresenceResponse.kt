package com.turik2304.coursework.network.calls

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetUserPresenceResponse(
    val presence: Presence
)

@Serializable
@SerialName("presence")
class Presence(
    val aggregated: Aggregated
)

@Serializable
@SerialName("aggregated")
class Aggregated(
    @SerialName("status")
    val status: String
)
