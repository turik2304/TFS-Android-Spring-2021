package com.turik2304.coursework.network

import com.turik2304.coursework.MyUserId
import com.turik2304.coursework.network.calls.ZulipMessage
import com.turik2304.coursework.network.calls.ZulipReaction
import com.turik2304.coursework.network.utils.NarrowConstructor
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.*
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

object ZulipAPICallHandler : CallHandler {

    override fun getStreamUIListFromServer(needAllStreams: Boolean): Single<List<StreamUI>> {
        if (needAllStreams) {
            return RetroClient.zulipApi.getAllStreams()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { allStreamsResponse ->
                        return@map allStreamsResponse.allStreams
                    }
        } else {
            return RetroClient.zulipApi.getSubscribedStreams()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { subscribedStreamsResponse ->
                        return@map subscribedStreamsResponse.subscribedStreams
                    }
        }
    }

    override fun getTopicsUIListByStreamUid(streamUid: Int): Single<List<TopicUI>> {
        return RetroClient.zulipApi.getTopics(streamUid)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { topicsResponse ->
                    topicsResponse.topics.forEach { topic ->
                        topic.streamUid = streamUid
                    }
                    return@map topicsResponse.topics
                }
    }

    override fun getOwnProfile(): Single<Pair<String, String>> {
        val getOwnProfile = RetroClient.zulipApi.getOwnProfile()
        val getOwnPresence = RetroClient.zulipApi.getUserPresence(MyUserId.MY_USER_ID.toString())
        return Single.zip(getOwnProfile, getOwnPresence,
                BiFunction { ownProfileResponse, ownPresence ->
                    val status = ownPresence.presence.aggregated.status
                    val ownProfileName = ownProfileResponse.name
                    return@BiFunction (ownProfileName to status)
                })
                .subscribeOn(Schedulers.io())
    }

    override fun updateMessageUIListAfterSendingMessage(
            nameOfTopic: String,
            nameOfStream: String,
            uidOfSentMessage: String,
            currentList: MutableList<ViewTyped>
    ): Single<List<ViewTyped>> {
        return getMessageUIListFromServer(nameOfTopic, nameOfStream, uidOfSentMessage, true)
                .map { response ->
                    val receivedMessage = response.singleOrNull()
                    val updatedList = mutableListOf<ViewTyped>()
                    if (receivedMessage != null) {
                        var indexOfUpdatedMessage = -1
                        currentList.forEachIndexed { index, message ->
                            if (message.uid == receivedMessage.uid) {
                                indexOfUpdatedMessage = index
                            }
                        }
                        updatedList.addAll(currentList)
                        updatedList[indexOfUpdatedMessage] = receivedMessage
                    }
                    return@map updatedList
                }
    }

    override fun getMessageUIListFromServer(
            nameOfTopic: String,
            nameOfStream: String,
            uidOfLastLoadedMessage: String,
            needOneMessage: Boolean
    ): Single<List<ViewTyped>> {
        val narrow = NarrowConstructor.getNarrow(nameOfTopic, nameOfStream)
        var numBefore = 20
        if (needOneMessage) {
            numBefore = 0
        }
        return RetroClient.zulipApi.getMessages(uidOfLastLoadedMessage, numBefore, 0, narrow)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { messagesResponse ->
                    messagesResponse.messages
                }
                .map { messageList ->
                    if (needOneMessage) {
                        return@map parseMessages(messageList)
                    } else {
                        return@map messageList
                                .sortedBy { it.dateInSeconds }
                                .groupBy { message ->
                                    getFormattedDate(message.dateInSeconds)
                                }
                                .flatMap { (date, messages) ->
                                    listOf(DateSeparatorUI(date, date.hashCode())) + parseMessages(
                                            messages
                                    )
                                }
                    }
                }
    }

    private fun parseMessages(remoteMessages: List<ZulipMessage>): List<ViewTyped> {
        val messageUIList = mutableListOf<ViewTyped>()
        remoteMessages.forEach { messageToken ->
            if (messageToken.userId == MyUserId.MY_USER_ID) {
                messageUIList.add(
                        OutMessageUI(
                                userName = messageToken.userName,
                                userId = messageToken.userId,
                                message = messageToken.message,
                                reactions = parseReactions(messageToken.reactions),
                                dateInSeconds = messageToken.dateInSeconds,
                                uid = messageToken.uid,
                        )
                )
            } else {
                messageUIList.add(
                        InMessageUI(
                                userName = messageToken.userName,
                                userId = messageToken.userId,
                                message = messageToken.message,
                                reactions = parseReactions(messageToken.reactions),
                                dateInSeconds = messageToken.dateInSeconds,
                                uid = messageToken.uid,
                        )
                )
            }
        }
        return messageUIList
    }

    private fun getFormattedDate(dateOfMessageInSeconds: Int): String {
        val formatter = SimpleDateFormat("dd MMMM")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateOfMessageInSeconds * 1000L
        return formatter.format(calendar.time)
    }

    private fun parseReactions(zulipReactions: List<ZulipReaction>): List<CallHandler.Reaction> {
        val listOfReactions = mutableListOf<CallHandler.Reaction>()
        zulipReactions.forEach { zulipReaction ->
            var isTheSameReaction = false
            var indexOfSameReaction = -1
            val emojiCode = Integer.parseInt(zulipReaction.emojiCode, 16)
            val userId = zulipReaction.userId
            listOfReactions.forEachIndexed { index, reaction ->
                if (reaction.emojiCode == emojiCode) {
                    isTheSameReaction = true
                    indexOfSameReaction = index
                }
            }
            if (isTheSameReaction) {
                listOfReactions[indexOfSameReaction].counter++
                listOfReactions[indexOfSameReaction].usersWhoClicked.add(userId)
            } else {
                listOfReactions.add(CallHandler.Reaction(emojiCode, 1, mutableListOf(userId)))
            }
        }
        return listOfReactions.reversed()
    }
}
