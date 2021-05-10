package com.turik2304.coursework.presentation.recycler_view.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.BaseViewHolder
import com.turik2304.coursework.presentation.recycler_view.base.RecyclerItemClicksObservable
import com.turik2304.coursework.presentation.recycler_view.items.UserUI
import com.turik2304.coursework.presentation.utils.SetStatusUtil.setColoredImageStatus

class UserHolder(
    view: View,
    clicks: RecyclerItemClicksObservable,
) : BaseViewHolder<UserUI>(view) {

    private val userName = view.findViewById<TextView>(R.id.tvUserNamePeopleTab)
    private val email = view.findViewById<TextView>(R.id.tvEmailOfUserPeopleTab)
    private val avatar = view.findViewById<ImageView>(R.id.imUserAvatarPeopleTab)
    private val status = view.findViewById<ImageView>(R.id.imUserStatus)

    init {
        clicks.accept(this)
        (view as ShimmerFrameLayout).hideShimmer()
        avatar.clipToOutline = true
    }

    override fun bind(item: UserUI) {
        userName.text = item.userName
        email.text = item.email
        status.setColoredImageStatus(item.presence)
        Glide.with(avatar)
            .load(item.avatarUrl)
            .into(avatar)
    }

}