package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Reducer

class ChatReducer : Reducer<ChatUiState, ChatActions> {
    override fun reduce(state: ChatUiState, action: ChatActions): ChatUiState {
        return when (action) {
            //Message longpolling reducing
            is ChatActions.RegisterMessageEvents -> ChatUiState(
                isLoading = state.isLoading
            )

            is ChatActions.MessageEventsRegistered -> ChatUiState(
                isLoading = state.isLoading,
                data = LoadedData.MessageLongpollingData(
                    messagesQueueId = action.queueId,
                    lastMessageEventId = action.eventId,
                )
            )

            is ChatActions.GetMessageEvents -> ChatUiState(
                isLoading = state.isLoading
            )

            is ChatActions.MessageEventReceived -> ChatUiState(
                data = LoadedData.MessageLongpollingData(
                    messagesQueueId = action.queueId,
                    lastMessageEventId = action.eventId,
                    polledData = action.updatedList
                )
            )

            //Reaction longpolling reducing
            is ChatActions.RegisterReactionEvents -> ChatUiState(
                isLoading = state.isLoading
            )
            is ChatActions.ReactionEventsRegistered -> ChatUiState(
                isLoading = state.isLoading,
                data = LoadedData.ReactionLongpollingData(
                    reactionsQueueId = action.queueId,
                    lastReactionEventId = action.eventId,
                )
            )

            is ChatActions.GetReactionEvents -> ChatUiState(
                isLoading = state.isLoading
            )

            is ChatActions.ReactionEventReceived -> ChatUiState(
                data = LoadedData.ReactionLongpollingData(
                    reactionsQueueId = action.queueId,
                    lastReactionEventId = action.eventId,
                    polledData = action.updatedList
                )
            )
//            is ChatActions.ReceivedEmptyEvent -> ChatUiState(
//                isLoading = state.isLoading,
//            )

            //Main reducing
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
            is ChatActions.SendMessage -> TODO()
            is ChatActions.AddReaction -> TODO()
            is ChatActions.RemoveReaction -> TODO()
        }
    }

}
