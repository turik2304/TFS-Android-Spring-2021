package com.turik2304.coursework.presentation.base

import com.jakewharton.rxrelay3.PublishRelay

interface MviView<A: Action, S: State> {
    val actions: PublishRelay<A>
    fun render(state: S)
}