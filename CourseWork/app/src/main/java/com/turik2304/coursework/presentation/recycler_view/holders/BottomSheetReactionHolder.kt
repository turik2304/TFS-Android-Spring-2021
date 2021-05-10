package com.turik2304.coursework.presentation.recycler_view.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.BaseViewHolder
import com.turik2304.coursework.presentation.recycler_view.base.RecyclerItemClicksObservable
import com.turik2304.coursework.presentation.recycler_view.items.BottomSheetReactionUI
import com.turik2304.coursework.presentation.recycler_view.items.UserUI
import com.turik2304.coursework.presentation.utils.SetStatusUtil.setColoredImageStatus

data class BottomSheetReactionHolder(
    val view: View,
    val clicks: RecyclerItemClicksObservable,
) : BaseViewHolder<BottomSheetReactionUI>(view) {

    init {
        clicks.accept(this)
    }

    override fun bind(item: BottomSheetReactionUI) {
        val emojiString = String(Character.toChars(item.emojiCode))
        (view as TextView).text = emojiString
    }

}