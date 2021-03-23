package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.ViewTyped

data class StreamAndTopicSeparatorUI(
    override val uid: String = "STREAM_TOPIC_SEPARATOR_UI_ID",
    override val viewType: Int = R.layout.item_stream_serarator,
) : ViewTyped {
}