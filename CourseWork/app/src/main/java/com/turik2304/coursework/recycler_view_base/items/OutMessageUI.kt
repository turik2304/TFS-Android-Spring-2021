package com.turik2304.coursework.recycler_view_base.items

import com.turik2304.coursework.R
import com.turik2304.coursework.network.models.data.Reaction
import com.turik2304.coursework.recycler_view_base.ViewTyped

data class OutMessageUI(
        val nameOfStream: String,
        val nameOfTopic: String,
        val userName: String,
        val userId: Int,
        val message: String,
        var reactions: List<Reaction>,
        val dateInSeconds: Int,
        override var uid: Int,
        override val viewType: Int = R.layout.item_outcoming_message
) : ViewTyped



