package com.turik2304.coursework.domain

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.presentation.UsersActions
import com.turik2304.coursework.presentation.UsersUiState
import io.reactivex.rxjava3.core.Observable

class OwnProfileMiddleware : Middleware<UsersActions, UsersUiState> {

    override val repository: Repository = ZulipRepository

    override fun bind(
        actions: Observable<UsersActions>,
        state: Observable<UsersUiState>
    ): Observable<UsersActions> {
        return actions.ofType(UsersActions.LoadUsers::class.java)
            .flatMap {
                return@flatMap repository.getOwnProfile()
                    .map<UsersActions> { result -> UsersActions.UsersLoaded(result) }
                    .onErrorReturn { error -> UsersActions.ErrorLoading(error) }
            }
    }
}