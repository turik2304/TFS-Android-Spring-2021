package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.network.CallHandler
import com.turik2304.coursework.recycler_view_base.ViewTyped

data class OutMessageUI(
    val userName: String,
    val userId: Int,
    val message: String,
    var reactions: List<CallHandler.Reaction>,
    val dateInSeconds: Int,
    override val uid: Int,
    override val viewType: Int = R.layout.item_outcoming_message
) : ViewTyped {


}
