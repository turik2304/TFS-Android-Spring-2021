package com.turik2304.coursework.data.network.utils

import com.turik2304.coursework.data.network.models.PreViewTyped
import com.turik2304.coursework.data.network.models.data.Message
import com.turik2304.coursework.data.network.models.data.Stream
import com.turik2304.coursework.data.network.models.data.Topic
import com.turik2304.coursework.data.network.models.data.User
import com.turik2304.coursework.data.network.utils.MessageHelperImpl.getFormattedDate
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.DateSeparatorUI
import com.turik2304.coursework.presentation.recycler_view.items.StreamUI
import com.turik2304.coursework.presentation.recycler_view.items.TopicUI
import com.turik2304.coursework.presentation.recycler_view.items.UserUI

object ViewTypedConverterImpl : ViewTypedConverter {

    override val messageHelper = MessageHelperImpl

    override fun <T : PreViewTyped> convertToViewTypedItems(modelList: List<T>): List<ViewTyped> {
        if (modelList.firstOrNull() is Message) {
            return (modelList as List<Message>)
                .sortedBy { it.dateInSeconds }
                .groupBy { message ->
                    getFormattedDate(message.dateInSeconds)
                }
                .flatMap { (date, messages) ->
                    listOf(
                        DateSeparatorUI(
                            date,
                            date.hashCode()
                        )
                    ) + messageHelper.parseMessages(
                        messages
                    )
                }
        } else {
            return modelList.mapNotNull { model ->
                when (model) {
                    is Stream -> StreamUI(
                        name = model.name,
                        uid = model.id,
                        topics = model.topics.toViewTyped(),
                        isSubscribed = model.isSubscribed,
                    )
                    is User -> UserUI(
                        userName = model.userName,
                        email = model.email,
                        avatarUrl = model.avatarUrl,
                        uid = model.id,
                        presence = model.presence,
                    )
                    else -> null
                }
            }
        }
    }


    private fun List<Topic>.toViewTyped(): List<TopicUI> {
        return map { topic ->
            TopicUI(
                name = topic.name,
                numberOfMessages = topic.numberOfMessages,
                uid = topic.id
            )
        }
    }
}
