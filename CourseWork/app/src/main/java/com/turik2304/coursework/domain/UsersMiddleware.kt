package com.turik2304.coursework.domain

import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.presentation.base.Action
import com.turik2304.coursework.presentation.base.UiState
import io.reactivex.rxjava3.core.Observable

class UsersMiddleware : Middleware<Action, UiState> {
    override fun bind(actions: Observable<Action>, state: Observable<UiState>): Observable<Action> {
        return actions.ofType(Action.LoadItems::class.java)
            .flatMap {
                return@flatMap ZulipRepository.getAllUsers()
                    .map<Action> { result -> Action.ItemsLoaded(result) }
                    .onErrorReturn { error -> Action.ErrorLoading(error) }
            }
    }
}