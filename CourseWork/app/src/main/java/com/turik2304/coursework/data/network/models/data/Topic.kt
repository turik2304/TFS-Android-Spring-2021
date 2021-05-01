package com.turik2304.coursework.data.network.models.data

import androidx.room.PrimaryKey
import com.turik2304.coursework.data.network.models.Model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Topic(
    @SerialName("name")
    val name: String,
    val numberOfMessages: String = "1240 mes",
    @PrimaryKey
    @SerialName("max_id")
    override val id: Int,
) : Model
