package com.turik2304.coursework.presentation.base

import com.turik2304.coursework.domain.Middleware
import io.reactivex.rxjava3.disposables.Disposable

abstract class Store<A, S>(
    val reducer: Reducer<S, A>,
    val middlewares: List<Middleware<A, S>>,
    private val initialState: S
) {
    abstract fun wire(): Disposable
    abstract fun bind(view: MviView<Action, UiState>): Disposable
}
