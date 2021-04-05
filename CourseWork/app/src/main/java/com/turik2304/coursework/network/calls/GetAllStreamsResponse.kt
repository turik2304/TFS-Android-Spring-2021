package com.turik2304.coursework.network.calls

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetAllStreamsResponse(
    @SerialName("streams")
    val allStreams: List<Stream>
)
