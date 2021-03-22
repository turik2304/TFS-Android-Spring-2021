package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.ViewTyped

data class TopicUI(
    val name: String,
    val numberOfMessages: String = "1240 mes",
    override val uid: String = "TOPIC_UI_ID",
    override var viewType: Int = R.layout.item_topic,
) : ViewTyped {
}