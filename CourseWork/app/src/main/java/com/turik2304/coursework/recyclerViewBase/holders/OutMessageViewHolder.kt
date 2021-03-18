package com.turik2304.coursework.recyclerViewBase.holders

import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.marginEnd
import com.turik2304.coursework.*
import com.turik2304.coursework.recyclerViewBase.BaseViewHolder
import com.turik2304.coursework.recyclerViewBase.items.InMessageUI
import com.turik2304.coursework.recyclerViewBase.items.OutMessageUI
import org.w3c.dom.Text
import kotlin.math.roundToInt

class OutMessageViewHolder(
    view: View,
    getDateInMillisClick: (View) -> Unit,
) : BaseViewHolder<OutMessageUI>(view) {

    private val outMessageHolder = view.findViewById<MessageViewGroup>(R.id.outMessageHolder)

    init {
        outMessageHolder.setOnLongClickListener { currentView ->
            getDateInMillisClick(currentView)
            true
        }

        outMessageHolder.flexboxLayout.imageViewAddsEmojis
            .setOnClickListener(getDateInMillisClick)
        outMessageHolder.userName.height = 0
        outMessageHolder.avatarImageView.layoutParams.width = 0
        outMessageHolder.removeViews(0, 2)
    }

    override fun bind(item: OutMessageUI) {
        outMessageHolder.message.text = item.message
        outMessageHolder.addReactions(item.reactions)
        outMessageHolder.dateInMillis = item.dateInMillis
        outMessageHolder.uid = item.uid
        outMessageHolder.flexboxLayout.checkZeroesCounters()
    }


}