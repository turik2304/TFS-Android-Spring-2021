package com.turik2304.coursework.presentation.recycler_view.holders

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.BaseViewHolder
import com.turik2304.coursework.presentation.recycler_view.base.RecyclerItemClicksObservable
import com.turik2304.coursework.presentation.recycler_view.items.TopicUI

class TopicHolder(
    view: View,
    clicks: RecyclerItemClicksObservable
) : BaseViewHolder<TopicUI>(view) {

    private val nameOfTopic = view.findViewById<TextView>(R.id.tvNameOfTopic)

    init {
        clicks.accept(this)
    }

    override fun bind(item: TopicUI) {
        nameOfTopic.text = item.nameOfTopic
        try {
            itemView.setBackgroundColor(Color.parseColor(item.streamColor))
        } catch (e: Exception) {
            itemView.setBackgroundColor(
                nameOfTopic.resources.getColor(
                    R.color.teal_700,
                    nameOfTopic.context.theme
                )
            )
        }

    }

}