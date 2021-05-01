package com.turik2304.coursework.data.network.models.response

import com.turik2304.coursework.data.network.models.data.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetAllUsersResponse(
    @SerialName("members")
    val members: List<User>
)