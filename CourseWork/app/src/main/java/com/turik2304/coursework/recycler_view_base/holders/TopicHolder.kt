package com.turik2304.coursework.recycler_view_base.holders

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.items.TopicUI
import kotlin.random.Random

class TopicHolder(
    view: View,
    startChatClick: (View) -> Unit
) : BaseViewHolder<TopicUI>(view) {

    private val nameOfTopic = view.findViewById<TextView>(R.id.tvNameOfTopic)
    private val numberOfMessages = view.findViewById<TextView>(R.id.tvNumberOfMessages)

    init {
        view.setOnClickListener(startChatClick)
        if (Random.nextBoolean()) {
            view.setBackgroundColor(Color.rgb(42, 157, 143))
        } else {
            view.setBackgroundColor(Color.rgb(233, 196, 106))
        }
    }


    override fun bind(item: TopicUI) {
        nameOfTopic.text = item.name
        numberOfMessages.text = item.numberOfMessages
    }

}