package com.turik2304.coursework.network.models.response

import com.turik2304.coursework.recycler_view_base.items.StreamUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetSubscribedStreamsResponse(
    @SerialName("subscriptions")
    val subscribedStreams: List<StreamUI>
)