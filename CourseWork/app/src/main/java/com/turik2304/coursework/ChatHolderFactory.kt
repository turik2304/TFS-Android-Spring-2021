package com.turik2304.coursework

import android.view.View
import com.turik2304.coursework.recyclerViewBase.BaseViewHolder
import com.turik2304.coursework.recyclerViewBase.HolderFactory
import com.turik2304.coursework.recyclerViewBase.holders.MessageViewHolder
import com.turik2304.coursework.recyclerViewBase.holders.TextViewHolder

class ChatHolderFactory(private val click: (View) -> Unit): HolderFactory() {

    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_message -> MessageViewHolder(view, click)
            R.layout.item_text -> TextViewHolder(view, click)
            else -> null
        }
    }
}