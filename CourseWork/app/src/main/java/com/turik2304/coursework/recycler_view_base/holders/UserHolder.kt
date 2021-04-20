package com.turik2304.coursework.recycler_view_base.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.turik2304.coursework.R
import com.turik2304.coursework.fragments.bottom_navigation_fragments.SetStatusUtil
import com.turik2304.coursework.fragments.bottom_navigation_fragments.SetStatusUtil.setColoredImageStatus
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.items.UserUI
import com.turik2304.coursework.stopAndHideShimmer

class UserHolder(
    view: View,
    startChatClick: (View) -> Unit,
) : BaseViewHolder<UserUI>(view) {

    private val userName = view.findViewById<TextView>(R.id.tvUserNamePeopleTab)
    private val email = view.findViewById<TextView>(R.id.tvEmailOfUserPeopleTab)
    private val avatar = view.findViewById<ImageView>(R.id.imUserAvatarPeopleTab)
    private val status = view.findViewById<ImageView>(R.id.imUserStatus)

    init {
        view.setOnClickListener(startChatClick)
        avatar.clipToOutline = true
    }

    override fun bind(item: UserUI) {
        if (item.profileDetailsLoadingStarted) {
            (itemView as ShimmerFrameLayout).showShimmer(true)
        } else {
            (itemView as ShimmerFrameLayout).stopAndHideShimmer()
        }
        userName.text = item.userName
        email.text = item.email
        status.setColoredImageStatus(item.presence)
        Glide.with(avatar)
                .load(item.avatarUrl)
                .into(avatar)
    }

}