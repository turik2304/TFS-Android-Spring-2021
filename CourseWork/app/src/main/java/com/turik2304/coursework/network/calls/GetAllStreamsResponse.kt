package com.turik2304.coursework.network.calls

import com.turik2304.coursework.recycler_view_base.items.StreamUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetAllStreamsResponse(
    @SerialName("streams")
    val allStreams: List<StreamUI>
)
