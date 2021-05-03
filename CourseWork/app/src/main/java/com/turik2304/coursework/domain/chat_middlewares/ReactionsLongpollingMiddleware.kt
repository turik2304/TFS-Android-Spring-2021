package com.turik2304.coursework.domain.chat_middlewares

import android.util.Log
import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.domain.Middleware
import com.turik2304.coursework.extensions.eventId
import com.turik2304.coursework.extensions.items
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatUiState
import io.reactivex.rxjava3.core.Observable

class ReactionsLongpollingMiddleware : Middleware<ChatActions, ChatUiState> {

    override val repository: Repository = ZulipRepository

    override fun bind(
        actions: Observable<ChatActions>,
        state: Observable<ChatUiState>
    ): Observable<ChatActions> {
        return actions.ofType(ChatActions.GetReactionEvents::class.java)
            .flatMap { action ->
                return@flatMap repository.getReactionEvent(
                    queueId = action.reactionsQueueId,
                    lastEventId = action.lastReactionEventId,
                    currentList = action.currentList,
                )
                    .map<ChatActions> { result ->
                        return@map ChatActions.MessageEventReceived(
                            queueId = action.reactionsQueueId,
                            eventId = result.eventId(),
                            updatedList = result.items()
                        )
                    }
                    .retry()
            }
    }
}

