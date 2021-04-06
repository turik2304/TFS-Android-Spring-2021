package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.ViewTyped
import io.taliox.zulip.calls.users.GetUserPresence
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserUI(
    @SerialName("full_name")
    val userName: String,
    @SerialName("email")
    val email: String,
    @SerialName("user_id")
    override val uid: Int,
    @SerialName("is_bot")
    var isBot: Boolean = false,
    var presence: String = "offline",
    override var viewType: Int = R.layout.item_user,
    var profileDetailsLoadingStarted: Boolean = false
) : ViewTyped {
}