package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.recycler_view_base.ViewTyped

data class InMessageUI(
    val userName: String,
    val userId: String,
    val message: String,
    var reactions: List<ServerApi.Reaction>,
    val dateInMillis: Long,
    override val uid: String = "INCOMING_MESSAGE_UI_ID",
    override val viewType: Int = R.layout.item_incoming_message
) : ViewTyped {


}
