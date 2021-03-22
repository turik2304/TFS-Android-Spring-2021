package com.turik2304.coursework.recycler_view_base.holders

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.items.UserUI

class UserHolder(
    view: View,
    startChatClick: (View) -> Unit
) : BaseViewHolder<UserUI>(view) {


    private val userName = view.findViewById<TextView>(R.id.tvUserNamePeopleTab)
    private val email = view.findViewById<TextView>(R.id.tvEmailOfUserPeopleTab)
    private val avatar = view.findViewById<ImageView>(R.id.imUserAvatarPeopleTab)
    private val userLinearLayout = view.findViewById<LinearLayout>(R.id.userLinearLayout)

    init {
        userLinearLayout.setOnClickListener(startChatClick)
        avatar.clipToOutline = true
    }


    override fun bind(item: UserUI) {
        userName.text = item.userName
        email.text = item.email
        userLinearLayout.tag = item.uid
    }

}