package com.turik2304.coursework.presentation

import android.util.Log
import com.turik2304.coursework.presentation.base.Reducer

class ChatReducer : Reducer<ChatUiState, ChatActions> {
    override fun reduce(state: ChatUiState, action: ChatActions): ChatUiState {
        return when (action) {

            is ChatActions.LoadItems -> ChatUiState(
                isLoading = true,
            )

            is ChatActions.ItemsLoaded ->
                if (action.isFirstPage) {
                    ChatUiState(
                        isLoading = false,
                        isFirstPage = true,
                        data = LoadedData.FirstPageData(action.items)
                    )
                } else {
                    ChatUiState(
                        isLoading = false,
                        isFirstPage = false,
                        data = LoadedData.NextPageData(action.items)
                    )
                }

            is ChatActions.LoadedEmptyList -> ChatUiState(
                isLoading = false
            )

            is ChatActions.ErrorLoading -> ChatUiState(
                isLoading = false,
                error = action.error
            )
            //Message longpolling actions
            is ChatActions.RegisterMessageEvents -> ChatUiState(
                isLoading = state.isLoading
            )

            is ChatActions.MessageEventsRegistered -> ChatUiState(
                isLoading = state.isLoading,
                data = LoadedData.LongpollingData(
                    messagesQueueId = action.queueId,
                    lastMessageEventId = action.eventId,
                )
            )

            is ChatActions.MessageEventReceived -> ChatUiState(
                data = LoadedData.LongpollingData(
                    messagesQueueId = action.queueId,
                    lastMessageEventId = action.eventId,
                    polledData = action.updatedList
                )
            )

            is ChatActions.GetMessageEvents -> ChatUiState(
                isLoading = state.isLoading
            )

            is ChatActions.ReceivedEmptyEvent -> ChatUiState(
                isLoading = state.isLoading,
            )
            is ChatActions.AddReaction -> TODO()
            is ChatActions.RemoveReaction -> TODO()
            is ChatActions.SendMessage -> TODO()

        }
    }

}