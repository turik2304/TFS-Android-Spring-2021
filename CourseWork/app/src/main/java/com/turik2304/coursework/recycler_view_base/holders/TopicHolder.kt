package com.turik2304.coursework.recycler_view_base.holders

import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.items.TopicUI
import kotlin.random.Random

class TopicHolder(
    view: View,
    startChatClick: (View) -> Unit
) : BaseViewHolder<TopicUI>(view) {

    private val nameOfTopicHolder = view.findViewById<TextView>(R.id.tvNameOfTopic)
    private val numberOfMessages = view.findViewById<TextView>(R.id.tvNumberOfMessages)
    private val topicLinearLayout = view.findViewById<LinearLayout>(R.id.topicLinearLayout)

    init {
        topicLinearLayout.setOnClickListener(startChatClick)
        if (Random.nextBoolean()) {
            topicLinearLayout.setBackgroundColor(Color.rgb(42, 157, 143))
        } else {
            topicLinearLayout.setBackgroundColor(Color.rgb(233, 196, 106))
        }
    }


    override fun bind(item: TopicUI) {
        nameOfTopicHolder.text = item.name
        numberOfMessages.text = item.numberOfMessages
        nameOfTopicHolder.tag = item.uid
    }

}