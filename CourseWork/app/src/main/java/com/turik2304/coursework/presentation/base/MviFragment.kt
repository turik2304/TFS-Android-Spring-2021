package com.turik2304.coursework.presentation.base

import androidx.fragment.app.Fragment

abstract class MviFragment<A : Action, S : State> : Fragment(), MviView<A, S> {
    abstract val store: Store<A, S>

}