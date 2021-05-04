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
        val setOfRawUidsOfMessages: HashSet<Int>
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

    data class ItemsLoaded(val items: List<ViewTyped>, val isFirstPage: Boolean) : ChatActions()

    object LoadedEmptyList : ChatActions()

    class ErrorLoading(val error: Throwable) : ChatActions()

    object SendMessage : ChatActions()

    object AddReaction : ChatActions()

    object RemoveReaction : ChatActions()
}
