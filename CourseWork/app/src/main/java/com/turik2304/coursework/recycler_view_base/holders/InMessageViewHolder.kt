package com.turik2304.coursework.recycler_view_base.holders

import android.view.View
import com.bumptech.glide.Glide
import com.turik2304.coursework.MessageViewGroup
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.items.InMessageUI

class InMessageViewHolder(
    view: View,
    getUidClick: (View) -> Unit,
) : BaseViewHolder<InMessageUI>(view) {

    private val inMessageHolder = view as MessageViewGroup

    init {
        inMessageHolder.setOnLongClickListener { currentView ->
            getUidClick(currentView)
            true
        }
        inMessageHolder.flexboxLayout.imageViewAddsEmojis
            .setOnClickListener(getUidClick)
    }

    override fun bind(item: InMessageUI) {
        inMessageHolder.userName.text = item.userName
        inMessageHolder.message.text = item.message
        inMessageHolder.addReactions(item.reactions)
        inMessageHolder.dateInSeconds = item.dateInSeconds
        inMessageHolder.uid = item.uid
        inMessageHolder.flexboxLayout.checkZeroesCounters()
        Glide.with(itemView).load(item.avatarUrl).into(inMessageHolder.avatarImageView)
    }
}