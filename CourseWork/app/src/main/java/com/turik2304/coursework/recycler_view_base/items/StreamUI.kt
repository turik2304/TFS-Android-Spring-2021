package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.ViewTyped

data class StreamUI(
    val name: String,
    override val uid: String = "STREAM_UI_ID",
    var isExpanded: Boolean = false,
    override val viewType: Int = R.layout.item_stream,
) : ViewTyped {
}