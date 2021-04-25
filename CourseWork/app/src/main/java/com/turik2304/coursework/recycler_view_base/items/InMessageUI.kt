package com.turik2304.coursework.recycler_view_base.items

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.turik2304.coursework.R
import com.turik2304.coursework.network.models.data.Reaction
import com.turik2304.coursework.recycler_view_base.ViewTyped

@Entity(tableName = "messages")
data class InMessageUI(
    val nameOfStream: String,
    val nameOfTopic: String,
    val userName: String,
    val userId: Int,
    val message: String,
    var reactions: List<Reaction> = emptyList(),
    val dateInSeconds: Int,
    val avatarUrl: String,
    @PrimaryKey
    override val uid: Int,
    override val viewType: Int = R.layout.item_incoming_message
) : ViewTyped



