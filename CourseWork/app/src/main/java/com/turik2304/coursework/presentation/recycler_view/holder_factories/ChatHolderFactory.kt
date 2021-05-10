package com.turik2304.coursework.presentation.recycler_view.holder_factories

import android.view.View
import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.BaseViewHolder
import com.turik2304.coursework.presentation.recycler_view.base.HolderFactory
import com.turik2304.coursework.presentation.recycler_view.holders.DateSeparatorHolder
import com.turik2304.coursework.presentation.recycler_view.holders.InMessageViewHolder
import com.turik2304.coursework.presentation.recycler_view.holders.OutMessageViewHolder

class ChatHolderFactory : HolderFactory() {

    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_incoming_message -> InMessageViewHolder(view, clicks)
            R.layout.item_outcoming_message -> OutMessageViewHolder(view, clicks)
            R.layout.item_date_separator -> DateSeparatorHolder(view)
            else -> null
        }
    }
}