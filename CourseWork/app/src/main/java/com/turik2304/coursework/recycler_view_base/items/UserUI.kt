package com.turik2304.coursework.recycler_view_base.items

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.turik2304.coursework.R
import com.turik2304.coursework.network.models.data.StatusEnum
import com.turik2304.coursework.recycler_view_base.ViewTyped
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "users")
@Serializable
data class UserUI(
        @SerialName("full_name")
        val userName: String,
        @SerialName("email")
        val email: String,
        @SerialName("avatar_url")
        val avatarUrl: String,
        @PrimaryKey
        @SerialName("user_id")
        override val uid: Int,
        @SerialName("is_bot")
        var isBot: Boolean = false,
        var presence: StatusEnum = StatusEnum.OFFLINE,
        override var viewType: Int = R.layout.item_user,
        var profileDetailsLoadingStarted: Boolean = false
) : ViewTyped