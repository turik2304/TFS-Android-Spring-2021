package com.turik2304.coursework.recyclerViewBase.holders

import android.view.View
import com.turik2304.coursework.MessageCustomViewGroup
import com.turik2304.coursework.R
import com.turik2304.coursework.recyclerViewBase.BaseViewHolder
import com.turik2304.coursework.recyclerViewBase.ViewTyped


class MessageUI(
//    val userName: String,
//    val avatar: ImageView,
    val message: String,
    override val viewType: Int = R.layout.item_message,
) : ViewTyped {

}

class MessageViewHolder(view: View, click: (View) -> Unit) : BaseViewHolder<MessageUI>(view) {

    val messageHolder = view.findViewById<MessageCustomViewGroup>(R.id.messageHolder)

    init {
//        messageHolder.setOnClickListener(click)
    }

    override fun bind(item: MessageUI) {
        messageHolder.message.text = item.message
//        messageHolder.text = item.message
//        messageHolder.message.text = item.message
//        messageHolder.userName.text = item.userName
    }
}