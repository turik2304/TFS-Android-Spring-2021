package com.turik2304.coursework.domain

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.presentation.GeneralActions
import com.turik2304.coursework.presentation.GeneralUiState
import io.reactivex.rxjava3.core.Observable

class OwnProfileMiddleware : Middleware<GeneralActions, GeneralUiState> {

    override val repository: Repository = ZulipRepository

    override fun bind(
        actions: Observable<GeneralActions>,
        state: Observable<GeneralUiState>
    ): Observable<GeneralActions> {
        return actions.ofType(GeneralActions.LoadItems::class.java)
            .flatMap {
                return@flatMap repository.getOwnProfile()
                    .map<GeneralActions> { result -> GeneralActions.ItemsLoaded(result) }
                    .onErrorReturn { error -> GeneralActions.ErrorLoading(error) }
            }
    }
}