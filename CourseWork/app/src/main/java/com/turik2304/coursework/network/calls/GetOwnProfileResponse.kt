package com.turik2304.coursework.network.calls

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetOwnProfileResponse(
    @SerialName("full_name")
    val name: String,
    @SerialName("email")
    val email: String
)

