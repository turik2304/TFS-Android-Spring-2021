package com.turik2304.coursework.presentation

import com.turik2304.coursework.data.network.models.data.MessageData
import com.turik2304.coursework.presentation.base.Reducer

class ChatReducer : Reducer<ChatUiState, ChatActions> {
    override fun reduce(state: ChatUiState, action: ChatActions): ChatUiState {
        return when (action) {
            //Longpolling reducing
            is ChatActions.RegisterEvents -> ChatUiState(
                isLoading = state.isLoading,
                messageClicked = state.messageClicked
            )
            is ChatActions.EventsRegistered -> ChatUiState(
                isLoading = state.isLoading,
                data = MessageData.EventRegistrationData(
                    messagesQueueId = action.messageQueueId,
                    messageEventId = action.messageEventId,
                    reactionsQueueId = action.reactionQueueId,
                    reactionEventId = action.reactionEventId
                ),
                messageClicked = state.messageClicked
            )
            is ChatActions.GetEvents -> ChatUiState(
                isLoading = state.isLoading,
                messageClicked = state.messageClicked
            )
            is ChatActions.MessageEventReceived -> ChatUiState(
                data = MessageData.MessageLongpollingData(
                    messagesQueueId = action.queueId,
                    lastMessageEventId = action.eventId,
                    polledData = action.updatedList
                ),
                messageClicked = state.messageClicked
            )
            is ChatActions.ReactionEventReceived -> ChatUiState(
                data = MessageData.ReactionLongpollingData(
                    reactionsQueueId = action.queueId,
                    lastReactionEventId = action.eventId,
                    polledData = action.updatedList
                ),
                messageClicked = state.messageClicked
            )
            //Main reducing
            is ChatActions.LoadItems -> ChatUiState(
                isLoading = true,
                messageClicked = state.messageClicked
            )
            is ChatActions.MessagesLoaded ->
                if (action.isFirstPage) {
                    ChatUiState(
                        isLoading = false,
                        isFirstPage = true,
                        data = MessageData.FirstPageData(action.messages),
                        messageClicked = state.messageClicked
                    )
                } else {
                    ChatUiState(
                        isLoading = false,
                        isFirstPage = false,
                        data = MessageData.NextPageData(action.messages),
                        messageClicked = state.messageClicked
                    )
                }

            is ChatActions.LoadedEmptyList -> ChatUiState(
                isLoading = true,
                messageClicked = state.messageClicked
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
                data = MessageData.SentMessageData(action.messages),
                messageClicked = state.messageClicked
            )
            is ChatActions.AddReaction -> ChatUiState(
                isLoading = true,
                messageClicked = false
            )
            is ChatActions.ReactionAdded -> ChatUiState(
                isLoading = false,
                messageClicked = false
            )
            is ChatActions.RemoveReaction -> ChatUiState(
                isLoading = true
            )
            is ChatActions.ReactionRemoved -> ChatUiState(
                isLoading = false
            )
            is ChatActions.GetBottomSheetReactions -> ChatUiState(
                isLoading = true
            )
            is ChatActions.BottomSheetReactionsReceived -> ChatUiState(
                isLoading = false,
                auxiliaryData = action.bottomSheetReactions
            )
            is ChatActions.ShowBottomSheetDialog -> ChatUiState(
                messageClicked = true
            )
            is ChatActions.DismissBottomSheetDialog -> ChatUiState(
                messageClicked = false
            )
        }
    }

}
