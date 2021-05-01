package com.turik2304.coursework.data.network.models.response

import com.turik2304.coursework.data.network.models.data.Stream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetSubscribedStreamsResponse(
    @SerialName("subscriptions")
    val subscribedStreams: List<Stream>
)