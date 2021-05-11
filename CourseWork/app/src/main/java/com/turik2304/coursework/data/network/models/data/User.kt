package com.turik2304.coursework.data.network.models.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.turik2304.coursework.data.network.models.PreViewTyped
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "users")
@Serializable
class User(
    @SerialName("full_name")
    val userName: String,
    @SerialName("email")
    val email: String,
    @SerialName("avatar_url")
    val avatarUrl: String,
    @PrimaryKey
    @SerialName("user_id")
    override val id: Int,
    @SerialName("is_bot")
    var isBot: Boolean = false,
    var presence: StatusEnum = StatusEnum.OFFLINE,
) : PreViewTyped