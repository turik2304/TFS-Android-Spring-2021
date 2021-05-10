package com.turik2304.coursework.presentation.recycler_view.holders

import android.graphics.Color
import android.view.View
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.BaseViewHolder
import com.turik2304.coursework.presentation.recycler_view.base.RecyclerItemClicksObservable
import com.turik2304.coursework.presentation.recycler_view.items.TopicUI
import kotlin.random.Random

class TopicHolder(
    view: View,
    startChatClick: RecyclerItemClicksObservable
) : BaseViewHolder<TopicUI>(view) {

    private val nameOfTopic = view.findViewById<TextView>(R.id.tvNameOfTopic)
    private val numberOfMessages = view.findViewById<TextView>(R.id.tvNumberOfMessages)

    init {
//        view.setOnClickListener(startChatClick)
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