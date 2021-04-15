package com.turik2304.coursework.recycler_view_base.items

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.turik2304.coursework.R
import com.turik2304.coursework.network.CallHandler
import com.turik2304.coursework.recycler_view_base.ViewTyped

@Entity(tableName = "messages")
data class InMessageUI(
    val userName: String,
    val userId: Int,
    val message: String,
    var reactions: List<CallHandler.Reaction> = emptyList(),
    val dateInSeconds: Int,
    @PrimaryKey
    override val uid: Int,
    override val viewType: Int = R.layout.item_incoming_message
) : ViewTyped



