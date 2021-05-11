package com.turik2304.coursework.data.network.models.response

import com.turik2304.coursework.data.network.models.data.StatusEnum
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetOwnProfileResponse(
    @SerialName("full_name")
    val name: String,
    @SerialName("email")
    val email: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    var statusEnum: StatusEnum = StatusEnum.OFFLINE
)