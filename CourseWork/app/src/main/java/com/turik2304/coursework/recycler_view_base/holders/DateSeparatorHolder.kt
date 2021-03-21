package com.turik2304.coursework.recycler_view_base.holders

import android.view.View
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.items.DateSeparatorUI

class DateSeparatorHolder(view: View) : BaseViewHolder<DateSeparatorUI>(view) {

    private val dateSeparatorHolder = view.findViewById<TextView>(R.id.dateSeparatorHolder)

    override fun bind(item: DateSeparatorUI) {
        dateSeparatorHolder.text = item.date
    }
}