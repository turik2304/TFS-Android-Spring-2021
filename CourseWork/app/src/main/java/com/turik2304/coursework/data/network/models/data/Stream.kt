package com.turik2304.coursework.data.network.models.data

import androidx.room.Entity
import com.turik2304.coursework.data.network.models.Model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "streams", primaryKeys = ["id", "isSubscribed"])
@Serializable
class Stream(
    @SerialName("name")
    val name: String,
    @SerialName("stream_id")
    override val id: Int,
    var topics: List<Topic> = emptyList(),
    var isSubscribed: Boolean = false,
) : Model