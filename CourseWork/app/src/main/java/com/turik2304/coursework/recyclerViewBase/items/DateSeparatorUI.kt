package com.turik2304.coursework.recyclerViewBase.items

import com.turik2304.coursework.R
import com.turik2304.coursework.recyclerViewBase.ViewTyped

data class DateSeparatorUI(
    val date: String,
    override val uid: String = "DATE_SEPARATOR_UI_ID",
    override val viewType: Int = R.layout.item_date_separator,
) : ViewTyped {
}