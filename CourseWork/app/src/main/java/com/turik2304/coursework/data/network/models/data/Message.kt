package com.turik2304.coursework.data.network.models.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.turik2304.coursework.data.network.models.RemoteModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "messages")
@Serializable
class Message(
    var nameOfStream: String? = null,
    var nameOfTopic: String? = null,
    @SerialName("sender_full_name")
    val userName: String,
    @SerialName("content")
    val message: String,
    @SerialName("timestamp")
    val dateInSeconds: Int,
    @SerialName("sender_id")
    val userId: Int,
    var reactions: List<Reaction>,
    @PrimaryKey
    @SerialName("id")
    override val id: Int,
    @SerialName("avatar_url")
    val avatarUrl: String
): RemoteModel