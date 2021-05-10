package com.turik2304.coursework.presentation.recycler_view.holders

import android.view.View
import com.turik2304.coursework.presentation.recycler_view.base.BaseViewHolder
import com.turik2304.coursework.presentation.recycler_view.base.RecyclerItemClicksObservable
import com.turik2304.coursework.presentation.recycler_view.items.OutMessageUI
import com.turik2304.coursework.presentation.view.MessageViewGroup

class OutMessageViewHolder(
    view: View,
    click: RecyclerItemClicksObservable,
) : BaseViewHolder<OutMessageUI>(view) {

    private val outMessageHolder = view as MessageViewGroup

    init {
        click.acceptLong(this)
        click.accept(outMessageHolder.flexboxLayout.imageViewAddsEmojis, this)
        outMessageHolder.userName.height = 0
        outMessageHolder.avatarImageView.layoutParams.width = 0
        outMessageHolder.removeViews(0, 2)
    }

    override fun bind(item: OutMessageUI) {
        outMessageHolder.message.text = item.message
        outMessageHolder.dateInSeconds = item.dateInSeconds
        outMessageHolder.uid = item.uid
        outMessageHolder.addReactions(item.reactions)
        outMessageHolder.flexboxLayout.checkZeroesCounters()
        outMessageHolder.isMyMessage = true
    }


}