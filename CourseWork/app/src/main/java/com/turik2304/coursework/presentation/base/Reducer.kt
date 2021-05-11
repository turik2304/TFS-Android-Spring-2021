package com.turik2304.coursework.presentation.base

interface Reducer<S : State, A : Action> {
    fun reduce(state: S, action: A): S
}