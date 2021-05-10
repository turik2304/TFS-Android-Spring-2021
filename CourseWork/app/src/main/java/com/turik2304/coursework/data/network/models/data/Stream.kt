package com.turik2304.coursework.data.network.models.data

import androidx.room.Entity
import com.turik2304.coursework.data.network.models.PreViewTyped
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "streams", primaryKeys = ["id", "isSubscribed"])
@Serializable
class Stream(
    @SerialName("name")
    val nameOfStream: String,
    @SerialName("color")
    val color: String,
    @SerialName("stream_id")
    override val id: Int,
    var topics: List<Topic> = emptyList(),
    var isSubscribed: Boolean = false,
) : PreViewTyped