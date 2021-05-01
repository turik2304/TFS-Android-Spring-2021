package com.turik2304.coursework.data.network.models.response

import com.turik2304.coursework.presentation.recycler_view.items.UserUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetUserResponse(
    @SerialName("user")
    val user: UserUI
)