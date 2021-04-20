package com.turik2304.coursework.network.models.data

data class Reaction(
    val emojiCode: Int,
    var counter: Int,
    val usersWhoClicked: MutableList<Int>
)