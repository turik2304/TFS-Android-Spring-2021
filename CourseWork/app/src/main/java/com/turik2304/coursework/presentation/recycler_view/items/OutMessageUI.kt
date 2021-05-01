package com.turik2304.coursework.presentation.recycler_view.items

import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

data class OutMessageUI(
    val userName: String,
    val userId: Int,
    val message: String,
    var reactions: List<ReactionUI>,
    val dateInSeconds: Int,
    override var uid: Int,
    override val viewType: Int = R.layout.item_outcoming_message
) : ViewTyped



