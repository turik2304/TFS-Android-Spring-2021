package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.ViewTyped

data class StreamAndTopicSeparatorUI(
    override val uid: Int,
    override val viewType: Int = R.layout.item_stream_serarator,
) : ViewTyped {
}