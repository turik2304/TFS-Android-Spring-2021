package com.turik2304.coursework.data.network.utils

import com.turik2304.coursework.data.network.models.PreViewTyped
import com.turik2304.coursework.data.network.models.data.*
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.*

class ViewTypedConverterImpl(override val messageHelper: MessageHelper) : ViewTypedConverter {

    override fun <T : PreViewTyped> convertToViewTypedItems(modelList: List<T>): List<ViewTyped> {
        if (modelList.firstOrNull() is Message) {
            return (modelList as List<Message>)
                .sortedBy { it.dateInSeconds }
                .groupBy { message ->
                    messageHelper.getFormattedDate(message.dateInSeconds)
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
                        nameOfStream = model.nameOfStream,
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
                    is BottomSheetReaction -> BottomSheetReactionUI(
                        emojiCode = model.emojiCode,
                        uid = model.id
                    )
                    else -> null
                }
            }
        }
    }


    private fun List<Topic>.toViewTyped(): List<TopicUI> {
        return map { topic ->
            TopicUI(
                nameOfTopic = topic.nameOfTopic,
                nameOfStream = topic.nameOfStream,
                streamColor = topic.streamColor,
                uid = topic.id
            )
        }
    }
}
