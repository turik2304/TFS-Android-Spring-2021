package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.recycler_view_base.ViewTyped

data class OutMessageUI(
    val userName: String,
    val userId: String,
    val message: String,
    var reactions: List<ServerApi.Reaction>,
    val dateInSeconds: Int,
    override val uid: String,
    override val viewType: Int = R.layout.item_outcoming_message
) : ViewTyped {


}
