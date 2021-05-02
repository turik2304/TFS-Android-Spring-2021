package com.turik2304.coursework.presentation.base

import com.jakewharton.rxrelay3.BehaviorRelay
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.domain.Middleware
import com.turik2304.coursework.extensions.plusAssign
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class Store<A: Action, S: State>(
    private val reducer: Reducer<S, A>,
    private val middlewares: List<Middleware<A, S>>,
    private val initialState: S
) {
    private val state = BehaviorRelay.createDefault(initialState)
    private val actions = PublishRelay.create<A>()

    fun wire(): Disposable {
        val disposable = CompositeDisposable()

        disposable += actions
            .withLatestFrom(state) { action, state ->
                reducer.reduce(state, action)
            }
            .distinctUntilChanged()
            .subscribe(state::accept)

        disposable += Observable.merge(
            middlewares.map { middleware ->
                middleware.bind(actions, state)
            }
        ).subscribe(actions::accept)

        return disposable
    }

    fun bind(view: MviView<A, S>): Disposable {
        val disposable = CompositeDisposable()
        disposable += state.observeOn(AndroidSchedulers.mainThread()).subscribe(view::render)
        disposable += view.actions.subscribe(actions::accept)
        return disposable
    }
}
