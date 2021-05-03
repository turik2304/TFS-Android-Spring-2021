package com.turik2304.coursework.domain.chat_middlewares

import android.util.Log
import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.domain.Middleware
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatUiState
import io.reactivex.rxjava3.core.Observable

class MessagesLongpollingMiddleware : Middleware<ChatActions, ChatUiState> {

    override val repository: Repository = ZulipRepository

    override fun bind(
        actions: Observable<ChatActions>,
        state: Observable<ChatUiState>
    ): Observable<ChatActions> {
        return actions.ofType(ChatActions.GetMessageEvents::class.java)
            .flatMap { action ->
                return@flatMap repository.getMessageEvent(
                    queueId = action.messagesQueueId,
                    lastEventId = action.lastMessageEventId,
                    nameOfTopic = action.nameOfTopic,
                    nameOfStream = action.nameOfStream,
                    currentList = action.currentList,
                    setOfRawUidsOfMessages = action.setOfRawUidsOfMessages
                )
                    .map<ChatActions> { result ->
                        return@map ChatActions.MessageEventReceived(
                            queueId = action.messagesQueueId,
                            eventId = result.first,
                            updatedList = result.second
                        )
                    }
                    .retry()
            }
    }
}

