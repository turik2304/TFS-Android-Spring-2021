package com.turik2304.coursework.data.network.models.data

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class Reaction(
    @PrimaryKey
    val emojiCode: Int,
    var counter: Int,
    val usersWhoClicked: MutableList<Int>
)