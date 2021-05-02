package com.turik2304.coursework.presentation.base

import androidx.appcompat.app.AppCompatActivity

abstract class MviActivity<A : Action, S : State> : AppCompatActivity(), MviView<A, S> {
    abstract val store: Store<A, S>
}