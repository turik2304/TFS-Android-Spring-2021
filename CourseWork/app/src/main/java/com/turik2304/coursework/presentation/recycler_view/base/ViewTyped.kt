package com.turik2304.coursework.presentation.recycler_view.base

interface ViewTyped {
    val viewType: Int
        get() = error("provide viewType $this")
    val uid: Int
        get() = error("provide uid for viewType $this")
}