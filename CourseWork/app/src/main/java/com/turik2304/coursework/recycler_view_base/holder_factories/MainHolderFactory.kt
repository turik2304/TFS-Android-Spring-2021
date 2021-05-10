package com.turik2304.coursework.recycler_view_base.holder_factories

import android.view.View
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.HolderFactory
import com.turik2304.coursework.recycler_view_base.holders.StreamHolder
import com.turik2304.coursework.recycler_view_base.holders.TopicHolder
import com.turik2304.coursework.recycler_view_base.holders.UserHolder
import com.turik2304.coursework.recycler_view_base.items.StreamAndTopicSeparatorUI

class MainHolderFactory(private val getContentClick: (View) -> Unit): HolderFactory() {

    override fun createViewHolder(view: View, viewType: Int): BaseViewHolder<*>? {
        return when (viewType) {
            R.layout.item_stream -> StreamHolder(view, getContentClick)
            R.layout.item_topic -> TopicHolder(view, getContentClick)
            R.layout.item_user -> UserHolder(view, getContentClick)
            R.layout.item_stream_serarator -> BaseViewHolder<StreamAndTopicSeparatorUI>(view)
            else -> null
        }
    }
}