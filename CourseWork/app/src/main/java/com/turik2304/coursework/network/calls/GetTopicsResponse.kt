package com.turik2304.coursework.network.calls

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GetTopicsResponse(
    @SerialName("topics")
    val topics: List<Topic>
)

@Serializable
data class Topic(
    @SerialName("name")
    val name: String,
)

