package com.turik2304.coursework.domain

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.presentation.UsersActions
import com.turik2304.coursework.presentation.UsersUiState
import io.reactivex.rxjava3.core.Observable

class OwnProfileMiddleware(override val repository: Repository) :
    Middleware<UsersActions, UsersUiState> {

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