package com.turik2304.coursework.recycler_view_base.holders

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.items.StreamUI

class StreamHolder(
    view: View,
    getTopicsClick: (View) -> Unit
) : BaseViewHolder<StreamUI>(view) {

    private val nameOfStreamHolder = view.findViewById<TextView>(R.id.tvNameOfStream)
    private val streamLinearLayout = view.findViewById<LinearLayout>(R.id.streamLinearLayout)

    init {
        streamLinearLayout.setOnClickListener(getTopicsClick)

    }

    override fun bind(item: StreamUI) {
        nameOfStreamHolder.text = item.name
        streamLinearLayout.tag = item.uid
    }
}