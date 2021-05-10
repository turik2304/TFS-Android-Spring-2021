package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.ViewTyped

data class UserUI(
    val userName: String,
    val email: String,
    override val uid: String,
    override var viewType: Int = R.layout.item_user,
) : ViewTyped {
}