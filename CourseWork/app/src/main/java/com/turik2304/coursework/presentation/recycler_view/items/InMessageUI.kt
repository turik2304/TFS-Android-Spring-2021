package com.turik2304.coursework.presentation.recycler_view.items

import com.turik2304.coursework.R
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

data class InMessageUI(
    val userName: String,
    val userId: Int,
    val message: String,
    var reactions: List<ReactionUI> = emptyList(),
    val dateInSeconds: Int,
    val avatarUrl: String,
    override val uid: Int,
    override val viewType: Int = R.layout.item_incoming_message
) : ViewTyped



