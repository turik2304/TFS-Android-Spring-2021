package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.ViewTyped

data class DateSeparatorUI(
    val date: String,
    override val uid: String,
    override val viewType: Int = R.layout.item_date_separator,
) : ViewTyped {
}