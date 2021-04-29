package com.turik2304.coursework.presentation.base

import io.reactivex.rxjava3.core.Observable

interface MviView<A, S> {
    val actions: Observable<A>
    fun render(state: S)
}