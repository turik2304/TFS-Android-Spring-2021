package com.turik2304.coursework.network.models.response

import com.turik2304.coursework.recycler_view_base.items.UserUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetAllUsersResponse(
    @SerialName("members")
    val members: List<UserUI>
)