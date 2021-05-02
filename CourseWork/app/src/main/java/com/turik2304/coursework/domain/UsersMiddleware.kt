package com.turik2304.coursework.domain

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.data.repository.ZulipRepository.toViewTypedItems
import com.turik2304.coursework.presentation.GeneralActions
import com.turik2304.coursework.presentation.base.UiState
import io.reactivex.rxjava3.core.Observable

class UsersMiddleware : Middleware<GeneralActions, UiState> {
    override fun bind(actions: Observable<GeneralActions>, state: Observable<UiState>): Observable<GeneralActions> {
        val repository: Repository = ZulipRepository
        return actions.ofType(GeneralActions.LoadItems::class.java)
            .flatMap {
                return@flatMap repository.getAllUsers().toViewTypedItems()
                    .map<GeneralActions> { result -> GeneralActions.ItemsLoaded(result) }
                    .onErrorReturn { error -> GeneralActions.ErrorLoading(error) }
            }
    }
}