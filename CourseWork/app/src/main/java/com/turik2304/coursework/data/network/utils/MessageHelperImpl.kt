package com.turik2304.coursework.data.network.utils

import com.turik2304.coursework.data.MyUserId
import com.turik2304.coursework.data.network.models.data.Message
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.InMessageUI
import com.turik2304.coursework.presentation.recycler_view.items.OutMessageUI
import java.text.SimpleDateFormat
import java.util.*

object MessageHelperImpl : MessageHelper {

    override val reactionHelper: ReactionHelper = ReactionHelperImpl

    override val setOfRawIdsOfMessages: HashSet<Int> = hashSetOf()

    private var uidOfRawMessage = -1

    override fun parseMessages(
        remoteMessages: List<Message>,
    ): List<ViewTyped> {
        val messageUIList = mutableListOf<ViewTyped>()
        remoteMessages.forEach { messageToken ->
            if (messageToken.userId == MyUserId.MY_USER_ID) {
                messageUIList.add(
                    OutMessageUI(
                        userName = messageToken.userName,
                        userId = messageToken.userId,
                        message = messageToken.message,
                        reactions = reactionHelper.parseReactions(messageToken.reactions),
                        dateInSeconds = messageToken.dateInSeconds,
                        uid = messageToken.id,
                    )
                )
            } else {
                messageUIList.add(
                    InMessageUI(
                        userName = messageToken.userName,
                        userId = messageToken.userId,
                        message = messageToken.message,
                        reactions = reactionHelper.parseReactions(messageToken.reactions),
                        dateInSeconds = messageToken.dateInSeconds,
                        avatarUrl = messageToken.avatarUrl,
                        uid = messageToken.id,
                    )
                )
            }
        }
        return messageUIList
    }

    override fun generateRawMessage(message: String): OutMessageUI {
        val rawMessage = OutMessageUI(
            userName = "",
            userId = MyUserId.MY_USER_ID,
            message = message,
            reactions = emptyList(),
            dateInSeconds = 0,
            uid = uidOfRawMessage--
        )
        setOfRawIdsOfMessages.add(rawMessage.uid)
        return rawMessage
    }

    override fun filterRawMessages(messages: List<ViewTyped>): List<ViewTyped> {
        return messages.filter { it.uid !in setOfRawIdsOfMessages }
    }

    override fun getFormattedDate(dateOfMessageInSeconds: Int): String {
        val formatter = SimpleDateFormat("dd MMMM")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateOfMessageInSeconds * 1000L
        return formatter.format(calendar.time)
    }
}