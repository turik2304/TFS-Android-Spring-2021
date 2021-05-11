package com.turik2304.coursework.domain.chat_middlewares

import com.turik2304.coursework.data.network.models.data.MessageData
import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.domain.Middleware
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatUiState
import io.reactivex.rxjava3.core.Observable

class EventsLongpollingMiddleware : Middleware<ChatActions, ChatUiState> {

    override val repository: Repository = ZulipRepository

    override fun bind(
        actions: Observable<ChatActions>,
        state: Observable<ChatUiState>
    ): Observable<ChatActions> {
        return actions.ofType(ChatActions.GetEvents::class.java)
            .flatMap { action ->
                return@flatMap repository.updateMessagesByEvents(
                    nameOfTopic = action.nameOfTopic,
                    nameOfStream = action.nameOfStream,
                    messageQueueId = action.messageQueueId,
                    messageEventId = action.messageEventId,
                    reactionQueueId = action.reactionQueueId,
                    reactionEventId = action.reactionEventId,
                    currentList = action.currentList,
                )
                    .map<ChatActions> { result ->
                        return@map when (result) {
                            is MessageData.MessageLongpollingData -> ChatActions.MessageEventReceived(
                                queueId = result.messagesQueueId,
                                eventId = result.lastMessageEventId,
                                updatedList = result.polledData
                            )
                            is MessageData.ReactionLongpollingData -> ChatActions.ReactionEventReceived(
                                queueId = result.reactionsQueueId,
                                eventId = result.lastReactionEventId,
                                updatedList = result.polledData
                            )
                            else -> action
                        }
                    }
                    .retry()
            }
    }
}

