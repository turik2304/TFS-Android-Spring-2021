package com.turik2304.coursework.recycler_view_base.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.BaseViewHolder
import com.turik2304.coursework.recycler_view_base.items.StreamUI

class StreamHolder(
    view: View,
    getTopicsClick: (View) -> Unit
) : BaseViewHolder<StreamUI>(view) {

    private val nameOfStreamHolder = view.findViewById<TextView>(R.id.tvNameOfStream)
    private val expandImageView = view.findViewById<ImageView>(R.id.imExpandStream)

    init {
        view.setOnClickListener(getTopicsClick)
    }

    override fun bind(item: StreamUI) {
        nameOfStreamHolder.text = item.name
        if (item.isExpanded) {
            expandImageView.setImageResource(R.drawable.ic_arrow_up_24)
            itemView.setBackgroundColor(
                itemView.resources.getColor(
                    R.color.gray_secondary_background,
                    itemView.context.theme
                )
            )
        } else {
            expandImageView.setImageResource(R.drawable.ic_arrow_down_24)
            itemView.setBackgroundColor(
                itemView.resources.getColor(
                    R.color.gray_primary_background,
                    itemView.context.theme
                )
            )
        }
    }
}