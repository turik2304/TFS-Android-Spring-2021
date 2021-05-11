package com.turik2304.coursework.presentation.recycler_view.items

data class ReactionUI(
    val emojiCode: Int,
    var counter: Int,
    val usersWhoClicked: MutableList<Int>
)