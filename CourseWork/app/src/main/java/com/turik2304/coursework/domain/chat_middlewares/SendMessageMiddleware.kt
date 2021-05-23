package com.turik2304.coursework.domain.chat_middlewares

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.domain.Middleware
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatUiState
import io.reactivex.rxjava3.core.Observable

class SendMessageMiddleware(override val repository: Repository) :
    Middleware<ChatActions, ChatUiState> {

    override fun bind(
        actions: Observable<ChatActions>,
        state: Observable<ChatUiState>
    ): Observable<ChatActions> {
        return actions.ofType(ChatActions.SendMessage::class.java)
            .flatMap { action ->
                return@flatMap repository.sendMessage(
                    nameOfTopic = action.nameOfTopic,
                    nameOfStream = action.nameOfStream,
                    message = action.message
                )
                    .map<ChatActions> { rawMessage ->
                        ChatActions.MessageSent(
                            messages = listOf(rawMessage)
                        )
                    }
                    .onErrorReturn { error -> ChatActions.ErrorLoading(error) }
            }
    }
}