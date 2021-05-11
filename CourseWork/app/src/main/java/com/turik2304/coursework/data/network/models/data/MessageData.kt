package com.turik2304.coursework.data.network.models.data

import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

sealed class MessageData {
    class FirstPageData(
        val items: List<ViewTyped>
    ) : MessageData()

    class NextPageData(
        val items: List<ViewTyped>
    ) : MessageData()

    class SentMessageData(
        val messages: List<ViewTyped>
    ) : MessageData()

    class EventRegistrationData(
        val messagesQueueId: String,
        val messageEventId: String,
        val reactionsQueueId: String,
        val reactionEventId: String
    ) : MessageData()

    class MessageLongpollingData(
        val messagesQueueId: String,
        val lastMessageEventId: String,
        val polledData: List<ViewTyped> = emptyList()
    ) : MessageData()

    class ReactionLongpollingData(
        val reactionsQueueId: String,
        val lastReactionEventId: String,
        val polledData: List<ViewTyped> = emptyList()
    ) : MessageData()

    object EmptyLongpollingData : MessageData()
}