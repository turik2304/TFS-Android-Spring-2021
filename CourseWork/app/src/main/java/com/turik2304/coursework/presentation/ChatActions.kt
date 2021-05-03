package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Action
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

sealed class ChatActions : Action {
    //Message longpolling actions
    class RegisterMessageEvents(
        val nameOfTopic: String,
        val nameOfStream: String
    ) : ChatActions()

    class MessageEventsRegistered(
        val eventId: String,
        val queueId: String
    ) : ChatActions()

    class GetMessageEvents(
        val messagesQueueId: String,
        val lastMessageEventId: String,
        val nameOfTopic: String,
        val nameOfStream: String,
        val currentList: List<ViewTyped>,
        val setOfRawUidsOfMessages: HashSet<Int>
    ) : ChatActions()

    class MessageEventReceived(
        val queueId: String,
        val eventId: String,
        val updatedList: List<ViewTyped>
    ) : ChatActions()

    //Reaction longpolling actions
    class RegisterReactionEvents(
        val nameOfTopic: String,
        val nameOfStream: String
    ) : ChatActions()

    class ReactionEventsRegistered(
        val eventId: String,
        val queueId: String
    ) : ChatActions()

    class GetReactionEvents(
        val reactionsQueueId: String,
        val lastReactionEventId: String,
        val currentList: List<ViewTyped>,
    ) : ChatActions()

    class ReactionEventReceived(
        val queueId: String,
        val eventId: String,
        val updatedList: List<ViewTyped>
    ) : ChatActions()

//    object ReceivedEmptyEvent : ChatActions()

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
