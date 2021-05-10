package com.turik2304.coursework.recycler_view_base.holders

import android.view.View
import com.turik2304.coursework.*
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.items.OutMessageUI

class OutMessageViewHolder(
    view: View,
    getDateInMillisClick: (View) -> Unit,
) : BaseViewHolder<OutMessageUI>(view) {

    private val outMessageHolder = view as MessageViewGroup

    init {
        outMessageHolder.setOnLongClickListener { currentView ->
            getDateInMillisClick(currentView)
            true
        }
        outMessageHolder.flexboxLayout.imageViewAddsEmojis
            .setOnClickListener(getDateInMillisClick)
        outMessageHolder.userName.height = 0
        outMessageHolder.avatarImageView.layoutParams.width = 0
        outMessageHolder.removeViews(0, 2)
    }

    override fun bind(item: OutMessageUI) {
        outMessageHolder.message.text = item.message
        outMessageHolder.addReactions(item.reactions)
        outMessageHolder.dateInMillis = item.dateInMillis
        outMessageHolder.uid = item.uid
        outMessageHolder.flexboxLayout.checkZeroesCounters()
        outMessageHolder.isMyMessage = true
    }


}