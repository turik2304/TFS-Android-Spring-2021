package com.turik2304.coursework.presentation.recycler_view.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.BaseViewHolder
import com.turik2304.coursework.presentation.recycler_view.base.RecyclerItemClicksObservable
import com.turik2304.coursework.presentation.recycler_view.items.StreamUI

class StreamHolder(
    view: View,
    clicks: RecyclerItemClicksObservable
) : BaseViewHolder<StreamUI>(view) {

    private val nameOfStreamHolder = view.findViewById<TextView>(R.id.tvNameOfStream)
    private val expandImageView = view.findViewById<ImageView>(R.id.imExpandStream)

    init {
        clicks.accept(this)
    }

    override fun bind(item: StreamUI) {
        nameOfStreamHolder.text = item.nameOfStream
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