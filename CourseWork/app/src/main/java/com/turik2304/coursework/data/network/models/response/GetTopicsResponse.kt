package com.turik2304.coursework.data.network.models.response

import com.turik2304.coursework.presentation.recycler_view.items.TopicUI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetTopicsResponse(
    @SerialName("topics")
    val topics: List<TopicUI>
)

