package com.turik2304.coursework.presentation.recycler_view.holders

import android.view.View
import com.bumptech.glide.Glide
import com.turik2304.coursework.presentation.recycler_view.base.BaseViewHolder
import com.turik2304.coursework.presentation.recycler_view.base.RecyclerItemClicksObservable
import com.turik2304.coursework.presentation.recycler_view.items.InMessageUI
import com.turik2304.coursework.presentation.view.MessageViewGroup

class InMessageViewHolder(
    view: View,
    clicks: RecyclerItemClicksObservable,
) : BaseViewHolder<InMessageUI>(view) {

    private val inMessageHolder = view as MessageViewGroup

    init {
        clicks.acceptLong(this)
        clicks.accept(inMessageHolder.flexboxLayout.imageViewAddsEmojis, this)
    }

    override fun bind(item: InMessageUI) {
        inMessageHolder.userName.text = item.userName
        inMessageHolder.message.text = item.message
        inMessageHolder.uid = item.uid
        inMessageHolder.addReactions(item.reactions)
        inMessageHolder.dateInSeconds = item.dateInSeconds
        inMessageHolder.flexboxLayout.checkZeroesCounters()
        Glide.with(itemView).load(item.avatarUrl).into(inMessageHolder.avatarImageView)
    }
}