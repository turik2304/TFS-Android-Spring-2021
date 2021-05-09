package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Action
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

sealed class ChatActions : Action {
    //Longpolling actions
    class RegisterEvents(
        val nameOfTopic: String,
        val nameOfStream: String
    ) : ChatActions()

    class EventsRegistered(
        val messageQueueId: String,
        val messageEventId: String,
        val reactionQueueId: String,
        val reactionEventId: String
    ) : ChatActions()

    class GetEvents(
        val nameOfTopic: String,
        val nameOfStream: String,
        val messageQueueId: String,
        val messageEventId: String,
        val reactionQueueId: String,
        val reactionEventId: String,
        val currentList: List<ViewTyped>,
    ) : ChatActions()

    class MessageEventReceived(
        val queueId: String,
        val eventId: String,
        val updatedList: List<ViewTyped>
    ) : ChatActions()

    class ReactionEventReceived(
        val queueId: String,
        val eventId: String,
        val updatedList: List<ViewTyped>
    ) : ChatActions()

    //Main actions
    data class LoadItems(
        val needFirstPage: Boolean = false,
        val nameOfTopic: String,
        val nameOfStream: String,
        val uidOfLastLoadedMessage: String
    ) : ChatActions()

    data class MessagesLoaded(val messages: List<ViewTyped>, val isFirstPage: Boolean) :
        ChatActions()

    object LoadedEmptyList : ChatActions()

    class ErrorLoading(val error: Throwable) : ChatActions()

    class SendMessage(
        val nameOfStream: String,
        val nameOfTopic: String,
        val message: String
    ) : ChatActions()

    class MessageSent(
        val messages: List<ViewTyped>
    ) : ChatActions()

    class AddReaction(
        val messageId: Int,
        val emojiName: String,
        val emojiCode: String
    ) : ChatActions()

    object ReactionAdded : ChatActions()

    class RemoveReaction(
        val messageId: Int,
        val emojiName: String,
        val emojiCode: String
    ) : ChatActions()

    object ReactionRemoved : ChatActions()
}
