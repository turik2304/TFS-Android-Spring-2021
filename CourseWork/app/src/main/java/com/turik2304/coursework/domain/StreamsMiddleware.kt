package com.turik2304.coursework.domain

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.presentation.UsersActions
import com.turik2304.coursework.presentation.UsersUiState
import io.reactivex.rxjava3.core.Observable

class StreamsMiddleware(private val needAllStreams: Boolean) :
    Middleware<UsersActions, UsersUiState> {

    override val repository: Repository = ZulipRepository

    override fun bind(
        actions: Observable<UsersActions>,
        state: Observable<UsersUiState>
    ): Observable<UsersActions> {
        return actions.ofType(UsersActions.LoadUsers::class.java)
            .flatMap {
                return@flatMap ZulipRepository.getStreams(needAllStreams = needAllStreams)
                    .map<UsersActions> { result ->
                        val streams = repository.converter.convertToViewTypedItems(result)
                        UsersActions.UsersLoaded(streams)
                    }
                    .onErrorReturn { error -> UsersActions.ErrorLoading(error) }
            }
    }
}