package com.turik2304.coursework.presentation.recycler_view.items

import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

data class DateSeparatorUI(
    val date: String,
    override val uid: Int,
    override val viewType: Int = R.layout.item_date_separator,
) : ViewTyped