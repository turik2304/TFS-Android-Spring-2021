package com.turik2304.coursework.presentation.recycler_view.holders

import android.view.View
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.BaseViewHolder
import com.turik2304.coursework.presentation.recycler_view.items.DateSeparatorUI

class DateSeparatorHolder(view: View) : BaseViewHolder<DateSeparatorUI>(view) {

    private val dateSeparatorHolder = view.findViewById<TextView>(R.id.dateSeparatorHolder)

    override fun bind(item: DateSeparatorUI) {
        dateSeparatorHolder.text = item.date
    }
}