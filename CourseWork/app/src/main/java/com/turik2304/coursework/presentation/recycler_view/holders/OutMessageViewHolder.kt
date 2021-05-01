package com.turik2304.coursework.presentation.recycler_view.holders

import android.view.View
import com.turik2304.coursework.presentation.recycler_view.base.BaseViewHolder
import com.turik2304.coursework.presentation.recycler_view.items.OutMessageUI
import com.turik2304.coursework.presentation.view.MessageViewGroup

class OutMessageViewHolder(
    view: View,
    getUidClick: (View) -> Unit,
) : BaseViewHolder<OutMessageUI>(view) {

    private val outMessageHolder = view as MessageViewGroup

    init {
        outMessageHolder.setOnLongClickListener { currentView ->
            getUidClick(currentView)
            true
        }
        outMessageHolder.flexboxLayout.imageViewAddsEmojis
            .setOnClickListener(getUidClick)
        outMessageHolder.userName.height = 0
        outMessageHolder.avatarImageView.layoutParams.width = 0
        outMessageHolder.removeViews(0, 2)
    }

    override fun bind(item: OutMessageUI) {
        outMessageHolder.message.text = item.message
        outMessageHolder.addReactions(item.reactions)
        outMessageHolder.dateInSeconds = item.dateInSeconds
        outMessageHolder.uid = item.uid
        outMessageHolder.flexboxLayout.checkZeroesCounters()
        outMessageHolder.isMyMessage = true
    }


}