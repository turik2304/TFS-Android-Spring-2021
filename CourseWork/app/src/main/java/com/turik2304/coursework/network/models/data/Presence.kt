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
    val status: String
)