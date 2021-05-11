package com.turik2304.coursework.data.network.models.response

import com.turik2304.coursework.data.network.models.data.Topic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetTopicsResponse(
    @SerialName("topics")
    val topics: List<Topic>
)

