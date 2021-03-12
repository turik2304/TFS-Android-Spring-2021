package com.turik2304.coursework.recyclerViewBase.holders

import android.util.Log
import android.view.View
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.recyclerViewBase.BaseViewHolder
import com.turik2304.coursework.recyclerViewBase.ViewTyped

class TextUI(
    val text: String,
    override val viewType: Int = R.layout.item_text,
) : ViewTyped


class TextViewHolder(view: View, click: (View) -> Unit) : BaseViewHolder<TextUI>(view) {

    val textHolder = view.findViewById<TextView>(R.id.textItem)

    init {
        textHolder.setOnClickListener(click)
    }

    override fun bind(item: TextUI) {
        textHolder.text = item.text
    }
}