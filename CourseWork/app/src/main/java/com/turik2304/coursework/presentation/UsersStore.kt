package com.turik2304.coursework.presentation

import com.jakewharton.rxrelay3.BehaviorRelay
import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.domain.UsersMiddleware
import com.turik2304.coursework.extensions.plusAssign
import com.turik2304.coursework.presentation.base.Action
import com.turik2304.coursework.presentation.base.MviView
import com.turik2304.coursework.presentation.base.Store
import com.turik2304.coursework.presentation.base.UiState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

class UsersStore : Store<Action, UiState>(
    reducer = UsersReducer(),
    middlewares = listOf(UsersMiddleware()),
    UiState()
) {
    private val state = BehaviorRelay.createDefault(UiState())
    private val actions = PublishRelay.create<Action>()

    override fun wire(): Disposable {
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

    override fun bind(view: MviView<Action, UiState>): Disposable {
        val disposable = CompositeDisposable()
        disposable += state.observeOn(AndroidSchedulers.mainThread()).subscribe(view::render)
        disposable += view.actions.subscribe(actions::accept)
        return disposable
    }
}