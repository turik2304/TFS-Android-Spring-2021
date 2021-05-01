package com.turik2304.coursework.extensions

import com.turik2304.coursework.data.MyUserId
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.DateSeparatorUI
import com.turik2304.coursework.presentation.recycler_view.items.InMessageUI
import com.turik2304.coursework.presentation.recycler_view.items.OutMessageUI

fun List<ViewTyped>.toInMessages(): List<InMessageUI> {
    return this.filter { it !is DateSeparatorUI }
        .map { message ->
            if (message is OutMessageUI) {
                return@map InMessageUI(
//                    nameOfStream = message.nameOfStream,
//                    nameOfTopic = message.nameOfTopic,
                    userName = message.userName,
                    userId = message.userId,
                    message = message.message,
                    reactions = message.reactions,
                    dateInSeconds = message.dateInSeconds,
                    avatarUrl = "",
                    uid = message.uid
                )
            } else return@map message as InMessageUI
        }
}

fun List<InMessageUI>.toViewTypedMessages(): List<ViewTyped> {
    return this.sortedBy { it.dateInSeconds }
        .groupBy { inMessage ->
            ZulipRepository.getFormattedDate(inMessage.dateInSeconds)
        }
        .flatMap { (date, inMessages) ->
            listOf(DateSeparatorUI(date, date.hashCode())) + parseIncomingMessages(inMessages)
        }

}

private fun parseIncomingMessages(inMessages: List<InMessageUI>): List<ViewTyped> {
    return inMessages.map { inMessage ->
        if (inMessage.userId == MyUserId.MY_USER_ID) {
            return@map OutMessageUI(
//                nameOfStream = inMessage.nameOfStream,
//                nameOfTopic = inMessage.nameOfTopic,
                userName = inMessage.userName,
                userId = inMessage.userId,
                message = inMessage.message,
                reactions = inMessage.reactions,
                dateInSeconds = inMessage.dateInSeconds,
                uid = inMessage.uid
            )
        } else return@map inMessage
    }
}
