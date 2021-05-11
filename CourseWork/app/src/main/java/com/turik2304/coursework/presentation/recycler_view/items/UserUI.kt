package com.turik2304.coursework.presentation.recycler_view.items

import com.turik2304.coursework.R
import com.turik2304.coursework.data.network.models.data.StatusEnum
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

data class UserUI(
    val userName: String,
    val email: String,
    val avatarUrl: String,
    override val uid: Int,
    var presence: StatusEnum = StatusEnum.OFFLINE,
    override var viewType: Int = R.layout.item_user,
) : ViewTyped