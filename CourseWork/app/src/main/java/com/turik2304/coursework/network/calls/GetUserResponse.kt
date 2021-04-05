package com.turik2304.coursework.network.calls

import com.turik2304.coursework.recycler_view_base.items.UserUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetUserResponse(
    @SerialName("user")
    val user: UserUI
)

