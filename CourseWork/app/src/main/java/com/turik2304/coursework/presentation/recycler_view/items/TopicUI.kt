package com.turik2304.coursework.presentation.recycler_view.items

import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

data class TopicUI(
    val nameOfTopic: String,
    val nameOfStream: String,
    val streamColor: String,
    override val uid: Int,
    override var viewType: Int = R.layout.item_topic,
) : ViewTyped
