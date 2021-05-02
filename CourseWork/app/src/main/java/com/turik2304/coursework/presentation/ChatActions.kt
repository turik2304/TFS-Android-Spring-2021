package com.turik2304.coursework.presentation

import com.turik2304.coursework.presentation.base.Action
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped

sealed class ChatActions : Action {

    class LoadItems(
        val needFirstPage: Boolean = false,
        val nameOfTopic: String,
        val nameOfStream: String,
        val uidOfLastLoadedMessage: String
    ) : ChatActions()

    object SendMessage : ChatActions()

    object AddReaction : ChatActions()

    object RemoveReaction : ChatActions()

    class ItemsLoaded(val items: List<ViewTyped>, val isFirstPage: Boolean) : ChatActions()

    object LoadedEmptyList : ChatActions()

    class ErrorLoading(val error: Throwable) : ChatActions()

}