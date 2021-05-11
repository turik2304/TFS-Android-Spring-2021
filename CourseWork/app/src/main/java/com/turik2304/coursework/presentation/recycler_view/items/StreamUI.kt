package com.turik2304.coursework.presentation.recycler_view.items

import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

data class StreamUI(
    val nameOfStream: String,
    override val uid: Int,
    var topics: List<TopicUI> = emptyList(),
    var isExpanded: Boolean = false,
    var isSubscribed: Boolean = false,
    override val viewType: Int = R.layout.item_stream,
) : ViewTyped