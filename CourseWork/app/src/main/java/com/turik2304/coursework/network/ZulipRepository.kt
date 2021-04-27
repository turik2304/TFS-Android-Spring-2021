package com.turik2304.coursework.network

import com.turik2304.coursework.MyApp
import com.turik2304.coursework.MyUserId
import com.turik2304.coursework.network.models.data.*
import com.turik2304.coursework.network.models.response.GetOwnProfileResponse
import com.turik2304.coursework.network.models.response.GetUserPresenceResponse
import com.turik2304.coursework.network.models.response.ResponseType
import com.turik2304.coursework.network.utils.NarrowConstructor
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.*
import com.turik2304.coursework.room.DatabaseClient
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet

object ZulipRepository : Repository {

    val db = DatabaseClient.getInstance(MyApp.app.applicationContext)

    override fun getAllUsers(): Observable<Pair<List<UserUI>, ResponseType>> {
        val usersFromDB =
            Observable.fromCallable {
                (db?.userDao()?.getAll() ?: emptyList()) to ResponseType.FROM_DB
            }
                .subscribeOn(Schedulers.io())
        val usersFromNetWork = RetroClient.zulipApi.getAllUsers()
            .subscribeOn(Schedulers.io())
            .map { response ->
                //users will be inserted after loading presences
                db?.userDao()?.deleteAll()
                val sortedList = response.members.sortedBy { user -> user.userName }
                return@map sortedList to ResponseType.FROM_NETWORK
            }
        return usersFromNetWork
            .publish { fromNetwork ->
                Observable.mergeDelayError(fromNetwork, usersFromDB.takeUntil(fromNetwork))
            }

    }

    override fun updateUserPresence(user: UserUI): Single<UserUI> {
        if (user.isBot) {
            return Single.just(user)
        } else {
            return RetroClient.zulipApi.getUserPresence(user.email)
                .subscribeOn(Schedulers.io())
                .map { response ->
                    val actualPresence = response.presence.aggregated.statusEnum
                    val updatedUser = user.copy(presence = actualPresence)
                    db?.userDao()?.insert(updatedUser)
                    return@map updatedUser
                }
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
            { ownProfileResponse: GetOwnProfileResponse, ownPresence: GetUserPresenceResponse ->
                ownProfileResponse.statusEnum = ownPresence.presence.aggregated.statusEnum
                return@zip ownProfileResponse
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

    override fun getMessageEvent(
        queueId: String,
        lastEventId: String,
        nameOfTopic: String,
        nameOfStream: String,
        currentList: List<ViewTyped>,
        queueOfRawUidsOfMessages: HashSet<Int>
    ): Observable<Pair<String, List<ViewTyped>>> {
        return RetroClient.zulipApi.getMessageEvents(queueId, lastEventId)
            .map { response ->
                if (response.messageEvents.isNotEmpty()) {
                    val newLastEventId = response.messageEvents.last().id
                    val zulipMessages = response.messageEvents.map { it.message }
                    val viewTypedMessages = parseMessages(zulipMessages, nameOfTopic, nameOfStream)
                    val newList = currentList.filter { it.uid !in queueOfRawUidsOfMessages }
                    val actualList = newList + viewTypedMessages
                    db?.messageDao()
                        ?.insertAllAndCheckCapacity(nameOfStream, nameOfTopic, viewTypedMessages)
                    return@map newLastEventId to actualList
                } else {
                    return@map lastEventId to emptyList<ViewTyped>()
                }
            }
    }

    override fun getReactionEvent(
        queueId: String,
        lastEventId: String,
        currentList: List<ViewTyped>,
    ): Observable<Pair<String, List<ViewTyped>>> {
        return RetroClient.zulipApi.getReactionEvents(queueId, lastEventId)
            .map { response ->
                if (response.reactionEvents.isNotEmpty()) {
                    val newLastEventId = response.reactionEvents.last().id
                    val reactionEvents = response.reactionEvents
                    val updatedList = updateReactions(currentList, reactionEvents)
                    return@map newLastEventId to updatedList
                } else return@map lastEventId to emptyList<ViewTyped>()
            }
    }

    override fun updateReactions(
        currentList: List<ViewTyped>,
        reactionEvents: List<ReactionEvent>
    ): List<ViewTyped> {
        var updatedList: List<ViewTyped> = currentList
        reactionEvents.forEach { reactionEvent ->
            updatedList = updatedList.map { viewTyped ->
                if (viewTyped.uid == reactionEvent.messageId) {
                    return@map when (viewTyped) {
                        is InMessageUI -> viewTyped.copy(
                            reactions = handleReactions(
                                viewTyped.reactions,
                                reactionEvent
                            )
                        )
                        is OutMessageUI -> viewTyped.copy(
                            reactions = handleReactions(
                                viewTyped.reactions,
                                reactionEvent
                            )
                        )
                        else -> viewTyped
                    }
                } else return@map viewTyped
            }
        }
        return updatedList
    }

    private fun handleReactions(
        reactions: List<Reaction>,
        reactionEvent: ReactionEvent
    ): List<Reaction> {
        val targetReaction = Reaction(
            emojiCode = Integer.parseInt(reactionEvent.emojiCode, 16),
            counter = 1,
            usersWhoClicked = mutableListOf(reactionEvent.userId)
        )
        var reactionFounded = false
        val updatedReactions = reactions.map { currentReaction ->
            if (currentReaction.emojiCode == targetReaction.emojiCode) {
                when (reactionEvent.operation) {
                    OperationEnum.ADD -> {
                        reactionFounded = true
                        if (targetReaction.usersWhoClicked.single() !in currentReaction.usersWhoClicked) {
                            val emojiCode = currentReaction.emojiCode
                            val counter = currentReaction.counter + 1
                            val usersWhoClicked =
                                (currentReaction.usersWhoClicked + targetReaction.usersWhoClicked).toMutableList()
                            return@map currentReaction.copy(
                                emojiCode = emojiCode,
                                counter = counter,
                                usersWhoClicked = usersWhoClicked
                            )
                        } else return@map currentReaction
                    }
                    OperationEnum.REMOVE -> {
                        reactionFounded = true
                        if (targetReaction.usersWhoClicked.single() in currentReaction.usersWhoClicked) {
                            val emojiCode = currentReaction.emojiCode
                            val counter = currentReaction.counter - 1
                            val usersWhoClicked =
                                (currentReaction.usersWhoClicked - targetReaction.usersWhoClicked).toMutableList()
                            return@map currentReaction.copy(
                                emojiCode = emojiCode,
                                counter = counter,
                                usersWhoClicked = usersWhoClicked
                            )
                        } else return@map currentReaction
                    }
                }
            } else return@map currentReaction
        }
        return if (reactionFounded) {
            updatedReactions.filter { it.counter > 0 }
        } else {
            return if (reactionEvent.operation == OperationEnum.ADD) {
                (updatedReactions + listOf(targetReaction))
            } else updatedReactions

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
