package com.turik2304.coursework.data.network.models.data

import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

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