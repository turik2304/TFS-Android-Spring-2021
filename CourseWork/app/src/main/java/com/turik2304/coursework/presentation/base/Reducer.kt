package com.turik2304.coursework.presentation.base

interface Reducer<S, A> {
    fun reduce(state: S, action: A): S
}