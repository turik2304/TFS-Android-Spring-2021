package com.turik2304.coursework.network

import com.turik2304.coursework.MyApp
import com.turik2304.coursework.MyUserId
import com.turik2304.coursework.network.models.data.Reaction
import com.turik2304.coursework.network.models.data.StatusEnum
import com.turik2304.coursework.network.models.data.ZulipMessage
import com.turik2304.coursework.network.models.data.ZulipReaction
import com.turik2304.coursework.network.models.response.GetOwnProfileResponse
import com.turik2304.coursework.network.utils.NarrowConstructor
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.*
import com.turik2304.coursework.room.DatabaseClient
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

object ZulipRepository : Repository {

    val db = DatabaseClient.getInstance(MyApp.app.applicationContext)

    fun getAllUsers(): Single<List<UserUI>> {
        return RetroClient.zulipApi.getAllUsers()
            .subscribeOn(Schedulers.io())
            .map { response ->
                //users will be inserted after loading presences
                db?.userDao()?.deleteAll()
                return@map response.members.sortedBy { user -> user.userName }
            }
    }

    fun updateUserPresence(user: UserUI): Single<StatusEnum> {
        return RetroClient.zulipApi.getUserPresence(user.email)
            .subscribeOn(Schedulers.io())
            .map { response ->
                user.presence = response.presence.aggregated.statusEnum
                db?.userDao()?.insert(user)
                return@map user.presence
            }
    }

    override fun getStreamUIListFromServer(needAllStreams: Boolean): Single<List<StreamUI>> {
        if (needAllStreams) {
            return RetroClient.zulipApi.getAllStreams()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { allStreamsResponse ->
                    //streams will be inserted after the topics are loaded
                    db?.streamDao()?.deleteStreams(deleteSubscriptions = false)
                    return@map allStreamsResponse.allStreams
                }
        } else {
            return RetroClient.zulipApi.getSubscribedStreams()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map map1@{ subscribedStreamsResponse ->
                    return@map1 subscribedStreamsResponse.subscribedStreams.map { stream ->
                        stream.isSubscribed = true
                        db?.streamDao()?.deleteStreams(deleteSubscriptions = true)
                        return@map stream
                    }
                }
        }
    }

    override fun updateTopicsOfStream(stream: StreamUI): Single<List<TopicUI>> {
        return RetroClient.zulipApi.getTopics(stream.uid)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { topicsResponse ->
                stream.topics = topicsResponse.topics
                db?.streamDao()?.insert(stream)
                return@map topicsResponse.topics
            }
    }

    override fun getOwnProfile(): Single<GetOwnProfileResponse> {
        val getOwnProfile = RetroClient.zulipApi.getOwnProfile()
        val getOwnPresence = RetroClient.zulipApi.getUserPresence(MyUserId.MY_USER_ID.toString())
        return Single.zip(getOwnProfile, getOwnPresence,
            BiFunction { ownProfileResponse, ownPresence ->
                ownProfileResponse.statusEnum = ownPresence.presence.aggregated.statusEnum
                return@BiFunction ownProfileResponse
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
        needOneMessage: Boolean,
        isFirstLoad: Boolean
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
                    val singleMessage = parseMessages(messageList, nameOfTopic, nameOfStream)
                    db?.messageDao()?.update(singleMessage)
                    return@map singleMessage
                } else {
                    return@map messageList
                        .sortedBy { it.dateInSeconds }
                        .groupBy { message ->
                            getFormattedDate(message.dateInSeconds)
                        }
                        .flatMap { (date, messages) ->
                            listOf(DateSeparatorUI(date, date.hashCode())) + parseMessages(
                                messages, nameOfTopic, nameOfStream
                            )
                        }
                }
            }
            .map { viewTypedList ->
                if (isFirstLoad) db?.messageDao()
                    ?.deleteAndCreate(nameOfStream, nameOfTopic, viewTypedList)
                else {
                    val numberOfMessagesCanBeInserted =
                        50 - (db?.messageDao()?.getCount(nameOfStream, nameOfTopic)
                            ?: 50)
                    val messagesToDatabase = viewTypedList.filter { it !is DateSeparatorUI }
                        .takeLast(numberOfMessagesCanBeInserted)
                    db?.messageDao()?.insertAll(messagesToDatabase)
                }
                return@map viewTypedList
            }
    }

    fun getMessageEvent(
        queueId: String,
        lastEventId: String,
        nameOfTopic: String,
        nameOfStream: String
    ): Observable<Pair<String, List<ViewTyped>>> {
        return RetroClient.zulipApi.getMessageEvents(queueId, lastEventId)
            .map { response ->
                val newLastEventId = response.events.last().id
                val zulipMessages = response.events.map { it.message }
                val viewTypedMessages = parseMessages(zulipMessages, nameOfTopic, nameOfStream)
                db?.messageDao()?.insertAllAndCheckCapacity(nameOfStream, nameOfTopic, viewTypedMessages)
                return@map newLastEventId to viewTypedMessages
            }
    }

    private fun parseMessages(
        remoteMessages: List<ZulipMessage>,
        nameOfTopic: String,
        nameOfStream: String
    ): List<ViewTyped> {
        val messageUIList = mutableListOf<ViewTyped>()
        remoteMessages.forEach { messageToken ->
            if (messageToken.userId == MyUserId.MY_USER_ID) {
                messageUIList.add(
                    OutMessageUI(
                        nameOfStream = nameOfStream,
                        nameOfTopic = nameOfTopic,
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
                        nameOfStream = nameOfStream,
                        nameOfTopic = nameOfTopic,
                        userName = messageToken.userName,
                        userId = messageToken.userId,
                        message = messageToken.message,
                        reactions = parseReactions(messageToken.reactions),
                        dateInSeconds = messageToken.dateInSeconds,
                        avatarUrl = messageToken.avatarUrl,
                        uid = messageToken.uid,
                    )
                )
            }
        }
        return messageUIList
    }

    override fun getFormattedDate(dateOfMessageInSeconds: Int): String {
        val formatter = SimpleDateFormat("dd MMMM")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateOfMessageInSeconds * 1000L
        return formatter.format(calendar.time)
    }

    private fun parseReactions(
        zulipReactions: List<ZulipReaction>,
    ): List<Reaction> {
        val listOfReactions = mutableListOf<Reaction>()
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
                listOfReactions.add(Reaction(emojiCode, 1, mutableListOf(userId)))
            }
        }
        return listOfReactions.reversed()
    }
}
