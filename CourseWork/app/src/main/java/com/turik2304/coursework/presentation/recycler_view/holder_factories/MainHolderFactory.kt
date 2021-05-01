package com.turik2304.coursework.presentation.recycler_view.holder_factories

import android.view.View
import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.BaseViewHolder
import com.turik2304.coursework.presentation.recycler_view.base.HolderFactory
import com.turik2304.coursework.presentation.recycler_view.holders.StreamHolder
import com.turik2304.coursework.presentation.recycler_view.holders.TopicHolder
import com.turik2304.coursework.presentation.recycler_view.holders.UserHolder

class MainHolderFactory(private val getContentClick: (View) -> Unit) : HolderFactory() {

    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_stream -> StreamHolder(view, getContentClick)
            R.layout.item_topic -> TopicHolder(view, getContentClick)
            R.layout.item_user -> UserHolder(view, getContentClick)
            else -> null
        }
    }
}