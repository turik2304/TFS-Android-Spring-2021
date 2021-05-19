package com.turik2304.coursework.domain.chat_middlewares

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.domain.Middleware
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatUiState
import io.reactivex.rxjava3.core.Observable

class AddReactionMiddleware(override val repository: Repository) :
    Middleware<ChatActions, ChatUiState> {

//    override val repository: Repository = ZulipRepository

    override fun bind(
        actions: Observable<ChatActions>,
        state: Observable<ChatUiState>
    ): Observable<ChatActions> {
        return actions.ofType(ChatActions.AddReaction::class.java)
            .flatMap { action ->
                return@flatMap repository.sendReaction(
                    messageId = action.messageId,
                    emojiName = action.emojiName,
                    emojiCode = action.emojiCode
                )
                    .andThen<ChatActions>(Observable.just(ChatActions.ReactionAdded))
                    .onErrorReturn { error -> ChatActions.ErrorLoading(error) }
            }
    }
}