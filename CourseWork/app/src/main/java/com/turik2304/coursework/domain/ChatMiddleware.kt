package com.turik2304.coursework.domain

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.data.repository.ZulipRepository.toViewTypedItems
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatUiState
import io.reactivex.rxjava3.core.Observable

class ChatMiddleware : Middleware<ChatActions, ChatUiState> {

    override val repository: Repository = ZulipRepository

    override fun bind(
        actions: Observable<ChatActions>,
        state: Observable<ChatUiState>
    ): Observable<ChatActions> {
        return actions.ofType(ChatActions.LoadItems::class.java)
            .flatMap { action ->
                return@flatMap repository.getMessages(
                    action.nameOfTopic,
                    action.nameOfStream,
                    action.uidOfLastLoadedMessage,
                    action.needFirstPage
                ).toViewTypedItems()
                    .map<ChatActions> { result ->
                        if (result.isNotEmpty()) {
                            return@map ChatActions.ItemsLoaded(
                                items = result,
                                isFirstPage = action.needFirstPage
                            )
                        } else return@map ChatActions.LoadedEmptyList

                    }
                    .onErrorReturn { error -> ChatActions.ErrorLoading(error) }
            }
    }
}