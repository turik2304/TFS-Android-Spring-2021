package com.turik2304.coursework.network.calls

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetSubscribedResponse(

    @SerialName("subscriptions")
    val subscribedStreams: List<Stream>
)

@Serializable
data class Stream(
    @SerialName("name")
    val name: String,
    @SerialName("stream_id")
    val uid: Int
)
