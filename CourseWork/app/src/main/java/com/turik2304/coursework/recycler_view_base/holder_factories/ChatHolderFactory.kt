package com.turik2304.coursework.recycler_view_base.holder_factories

import android.view.View
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.HolderFactory
import com.turik2304.coursework.recycler_view_base.holders.DateSeparatorHolder
import com.turik2304.coursework.recycler_view_base.holders.InMessageViewHolder
import com.turik2304.coursework.recycler_view_base.holders.OutMessageViewHolder

class ChatHolderFactory(private val getDateClick: (View) -> Unit) : HolderFactory() {

    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_incoming_message -> InMessageViewHolder(view, getDateClick)
            R.layout.item_outcoming_message -> OutMessageViewHolder(view, getDateClick)
            R.layout.item_date_separator -> DateSeparatorHolder(view)
            else -> null
        }
    }
}