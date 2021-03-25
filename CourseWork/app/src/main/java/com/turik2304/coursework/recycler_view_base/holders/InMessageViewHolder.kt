package com.turik2304.coursework.recycler_view_base.holders

import android.view.View
import com.turik2304.coursework.MessageViewGroup
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.items.InMessageUI

class InMessageViewHolder(
    view: View,
    getDateInMillisClick: (View) -> Unit,
) : BaseViewHolder<InMessageUI>(view) {

    private val inMessageHolder = view as MessageViewGroup

    init {
        inMessageHolder.setOnLongClickListener { currentView ->
            getDateInMillisClick(currentView)
            true
        }
        inMessageHolder.flexboxLayout.imageViewAddsEmojis
            .setOnClickListener(getDateInMillisClick)
    }

    override fun bind(item: InMessageUI) {
        inMessageHolder.userName.text = item.userName
        inMessageHolder.message.text = item.message
        inMessageHolder.addReactions(item.reactions)
        inMessageHolder.dateInMillis = item.dateInMillis
        inMessageHolder.uid = item.uid
        inMessageHolder.flexboxLayout.checkZeroesCounters()
    }
}