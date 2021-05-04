package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.State
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

data class ChatUiState(
    val isLoading: Boolean = false,
    val isFirstPage: Boolean = false,
    val data: LoadedData? = null,
    val error: Throwable? = null,
) : State

sealed class LoadedData {
    class FirstPageData(
        val items: List<ViewTyped>
    ) : LoadedData()

    class NextPageData(
        val items: List<ViewTyped>
    ) : LoadedData()

    class EventRegistrationData(
        val messagesQueueId: String,
        val messageEventId: String,
        val reactionsQueueId: String,
        val reactionEventId: String
    ) : LoadedData()

    class MessageLongpollingData(
        val messagesQueueId: String,
        val lastMessageEventId: String,
        val polledData: List<ViewTyped> = emptyList()
    ) : LoadedData()

    class ReactionLongpollingData(
        val reactionsQueueId: String,
        val lastReactionEventId: String,
        val polledData: List<ViewTyped> = emptyList()
    ) : LoadedData()

    object EmptyLongpollingData : LoadedData()
}