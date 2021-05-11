package com.turik2304.coursework.data.network.models.data

import androidx.room.PrimaryKey
import com.turik2304.coursework.data.network.models.PreViewTyped
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Topic(
    @SerialName("name")
    val nameOfTopic: String,
    var nameOfStream: String = "",
    var streamColor: String = "",
    @PrimaryKey
    @SerialName("max_id")
    override val id: Int,
) : PreViewTyped
