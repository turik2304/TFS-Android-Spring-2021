package com.turik2304.coursework.domain.chat_middlewares

import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.domain.Middleware
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatUiState
import io.reactivex.rxjava3.core.Observable

class RegisterEventsMiddleware : Middleware<ChatActions, ChatUiState> {

    override val repository: Repository = ZulipRepository

    override fun bind(
        actions: Observable<ChatActions>,
        state: Observable<ChatUiState>
    ): Observable<ChatActions> {
        return actions.ofType(ChatActions.RegisterEvents::class.java)
            .flatMap { action ->
                return@flatMap repository.registerEvents(
                    nameOfTopic = action.nameOfTopic,
                    nameOfStream = action.nameOfStream
                )
                    .map<ChatActions> { result ->
                        ChatActions.EventsRegistered(
                            messageQueueId = result.messagesQueueId,
                            messageEventId = result.messageEventId,
                            reactionQueueId = result.reactionsQueueId,
                            reactionEventId = result.reactionEventId
                        )
                    }
                    .retry()
            }
    }
}