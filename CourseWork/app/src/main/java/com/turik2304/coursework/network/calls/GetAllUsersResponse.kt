package com.turik2304.coursework.network.calls

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetAllUsersResponse(
    @SerialName("members")
    val members: List<User>
)

@Serializable
data class User(
    @SerialName("full_name")
    val userName: String,
    @SerialName("email")
    val email: String,
    @SerialName("user_id")
    val uid: Int
)
