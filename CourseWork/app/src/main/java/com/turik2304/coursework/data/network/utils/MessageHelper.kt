package com.turik2304.coursework.data.network.utils

import com.turik2304.coursework.data.network.models.data.Message
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.OutMessageUI

interface MessageHelper {
    val reactionHelper: ReactionHelper
    val setOfRawIdsOfMessages: HashSet<Int>

    fun parseMessages(
        remoteMessages: List<Message>,
    ): List<ViewTyped>

    fun getFormattedDate(dateOfMessageInSeconds: Int): String
    fun generateRawMessage(message: String): OutMessageUI
    fun filterRawMessages(messages: List<ViewTyped>): List<ViewTyped>
}
