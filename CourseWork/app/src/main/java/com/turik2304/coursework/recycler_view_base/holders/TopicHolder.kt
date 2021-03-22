package com.turik2304.coursework.recycler_view_base.holders

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.items.TopicUI

class TopicHolder(
    view: View,
    startChatClick: (View) -> Unit
) : BaseViewHolder<TopicUI>(view) {

    private val nameOfTopicHolder = view.findViewById<TextView>(R.id.tvNameOfTopic)
    private val numberOfMessages = view.findViewById<TextView>(R.id.tvNumberOfMessages)
    private val topicLinearLayout = view.findViewById<LinearLayout>(R.id.topicLinearLayout)

    init {
        topicLinearLayout.setOnClickListener(startChatClick)
    }


    override fun bind(item: TopicUI) {
        nameOfTopicHolder.text = item.name
        numberOfMessages.text = item.numberOfMessages
        nameOfTopicHolder.tag = item.uid
    }

}