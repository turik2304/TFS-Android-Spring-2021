package com.turik2304.coursework.presentation

import android.util.Log
import com.turik2304.coursework.data.network.models.data.MessageData
import com.turik2304.coursework.presentation.base.Reducer

class ChatReducer : Reducer<ChatUiState, ChatActions> {
    override fun reduce(state: ChatUiState, action: ChatActions): ChatUiState {
        return when (action) {
            //Longpolling reducing
            is ChatActions.RegisterEvents -> ChatUiState(
                isLoading = state.isLoading
            )
            is ChatActions.EventsRegistered -> ChatUiState(
                isLoading = state.isLoading,
                data = MessageData.EventRegistrationData(
                    messagesQueueId = action.messageQueueId,
                    messageEventId = action.messageEventId,
                    reactionsQueueId = action.reactionQueueId,
                    reactionEventId = action.reactionEventId
                )
            )
            is ChatActions.GetEvents -> ChatUiState(
                isLoading = state.isLoading
            )
            is ChatActions.MessageEventReceived -> ChatUiState(
                data = MessageData.MessageLongpollingData(
                    messagesQueueId = action.queueId,
                    lastMessageEventId = action.eventId,
                    polledData = action.updatedList
                )
            )
            is ChatActions.ReactionEventReceived -> ChatUiState(
                data = MessageData.ReactionLongpollingData(
                    reactionsQueueId = action.queueId,
                    lastReactionEventId = action.eventId,
                    polledData = action.updatedList
                )
            )
            //Main reducing
            is ChatActions.LoadItems -> ChatUiState(
                isLoading = true,
            )
            is ChatActions.MessagesLoaded ->
                if (action.isFirstPage) {
                    ChatUiState(
                        isLoading = false,
                        isFirstPage = true,
                        data = MessageData.FirstPageData(action.messages)
                    )
                } else {
                    ChatUiState(
                        isLoading = false,
                        isFirstPage = false,
                        data = MessageData.NextPageData(action.messages)
                    )
                }

            is ChatActions.LoadedEmptyList -> ChatUiState(
                isLoading = false
            )
            is ChatActions.ErrorLoading -> ChatUiState(
                isLoading = false,
                error = action.error
            )
            is ChatActions.SendMessage -> ChatUiState(
                isLoading = true
            )
            is ChatActions.MessageSent -> ChatUiState(
                isLoading = false,
                data = MessageData.SentMessageData(action.messages)
            )
            is ChatActions.AddReaction -> ChatUiState(
                isLoading = true
            )
            is ChatActions.ReactionAdded -> ChatUiState(
                isLoading = false
            )
            is ChatActions.RemoveReaction -> ChatUiState(
                isLoading = true
            )
            is ChatActions.ReactionRemoved -> ChatUiState(
                isLoading = false
            )
        }
    }

}
