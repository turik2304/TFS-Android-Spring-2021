package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.network.models.data.Reaction
import com.turik2304.coursework.recycler_view_base.ViewTyped

data class InMessageUI(
    val userName: String,
    val userId: Int,
    val message: String,
    var reactions: List<Reaction>,
    val dateInSeconds: Int,
    val avatarUrl: String,
    override val uid: Int,
    override val viewType: Int = R.layout.item_incoming_message
) : ViewTyped {


}
