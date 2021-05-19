package com.turik2304.coursework.domain.chat_middlewares

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.domain.Middleware
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatUiState
import io.reactivex.rxjava3.core.Observable

class RemoveReactionMiddleware(override val repository: Repository) :
    Middleware<ChatActions, ChatUiState> {

//    override val repository: Repository = ZulipRepository

    override fun bind(
        actions: Observable<ChatActions>,
        state: Observable<ChatUiState>
    ): Observable<ChatActions> {
        return actions.ofType(ChatActions.RemoveReaction::class.java)
            .flatMap { action ->
                return@flatMap repository.removeReaction(
                    messageId = action.messageId,
                    emojiName = action.emojiName,
                    emojiCode = action.emojiCode
                )
                    .andThen<ChatActions>(Observable.just(ChatActions.ReactionRemoved))
                    .onErrorReturn { error -> ChatActions.ErrorLoading(error) }
            }
    }
}