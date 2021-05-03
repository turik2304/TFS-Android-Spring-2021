package com.turik2304.coursework.extensions

import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

fun Pair<String, List<ViewTyped>>.eventId(): String {
    return first
}

fun Pair<String, List<ViewTyped>>.items(): List<ViewTyped> {
    return second
}