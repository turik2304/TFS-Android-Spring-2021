package com.turik2304.coursework.data.network.models.response

import com.turik2304.coursework.data.network.models.data.Presence
import kotlinx.serialization.Serializable

@Serializable
class GetUserPresenceResponse(
    val presence: Presence
)