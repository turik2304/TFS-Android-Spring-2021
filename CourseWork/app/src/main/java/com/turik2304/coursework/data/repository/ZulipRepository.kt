package com.turik2304.coursework.data.repository

import com.turik2304.coursework.MyApp
import com.turik2304.coursework.data.MyUserId
import com.turik2304.coursework.data.network.RetroClient
import com.turik2304.coursework.data.network.models.RemoteModel
import com.turik2304.coursework.data.network.models.data.*
import com.turik2304.coursework.data.network.models.response.GetOwnProfileResponse
import com.turik2304.coursework.data.network.models.response.GetUserPresenceResponse
import com.turik2304.coursework.data.network.utils.NarrowConstructor
import com.turik2304.coursework.data.room.DatabaseClient
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

object ZulipRepository : Repository {

    private const val NUMBER_OF_MESSAGES_BEFORE = 20
    private const val NUMBER_OF_MESSAGES_AFTER = 0
    private const val MAX_NUMBER_OF_MESSAGES_IN_DB = 50

    private val db = DatabaseClient.getInstance(MyApp.app.applicationContext)

    override fun <T : RemoteModel> Observable<List<T>>.toViewTypedItems(): Observable<List<ViewTyped>> {
        return this.observeOn(Schedulers.computation())
            .map { modelList ->
                if (modelList.firstOrNull() is Message) {
                    return@map (modelList as List<Message>)
                        .sortedBy { it.dateInSeconds }
                        .groupBy { message ->
                            getFormattedDate(message.dateInSeconds)
                        }
                        .flatMap { (date, messages) ->
                            listOf(DateSeparatorUI(date, date.hashCode())) + parseMessages(
                                messages
                            )
                        }
                } else {
                    modelList.mapNotNull { model ->
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

    override fun getAllUsers(): Observable<List<User>> {
        val usersFromDB =
            Observable.fromCallable {
                (db?.userDao()?.getAll() ?: emptyList())
            }
                .subscribeOn(Schedulers.io())
        val usersFromNetWork = RetroClient.zulipApi.getAllUsers()
            .subscribeOn(Schedulers.io())
            .flatMap { response ->
                val sortedUserList = response.members.sortedBy { user -> user.userName }
                Observable.fromIterable(sortedUserList)
                    .concatMap { user ->
                        return@concatMap if (user.isBot) {
                            Observable.just(user)
                        } else {
                            RetroClient.zulipApi.getUserPresence(user.email)
                                .map m@{ response ->
                                    user.presence = response.presence.aggregated.statusEnum
                                    return@m user
                                }
                                .toObservable()
                        }
                    }
                    .toList()
                    .toObservable()
                    .doOnNext { updatedUserList ->
                        db?.userDao()?.deleteAndCreate(updatedUserList)
                    }
            }
        return usersFromNetWork
            .publish { fromNetwork ->
                Observable.mergeDelayError(fromNetwork, usersFromDB.takeUntil(fromNetwork))
                    .onErrorResumeWith(usersFromDB)
            }
    }

    override fun getStreams(needAllStreams: Boolean): Observable<List<Stream>> {
        val streamsFromDB = Observable.fromCallable {
            (db?.streamDao()?.getStreams(needAllStreams) ?: emptyList())
        }
            .subscribeOn(Schedulers.io())
        val streamsFromNetwork: Observable<List<Stream>>
        if (needAllStreams) {
            streamsFromNetwork = RetroClient.zulipApi.getAllStreams()
                .subscribeOn(Schedulers.io())
                .flatMap { allStreamsResponse ->
                    getTopicsOfStreams(allStreamsResponse.allStreams)
                }
                .doOnNext { updatedStreams ->
                    db?.streamDao()?.deleteAndCreate(deleteAllStreams = true, updatedStreams)
                }
        } else {
            streamsFromNetwork = RetroClient.zulipApi.getSubscribedStreams()
                .subscribeOn(Schedulers.io())
                .flatMap { subscribedStreamsResponse ->
                    val subscribedStreams =
                        subscribedStreamsResponse.subscribedStreams.map { stream ->
                            stream.isSubscribed = true
                            return@map stream
                        }
                    getTopicsOfStreams(subscribedStreams)
                }
                .doOnNext { updatedStreams ->
                    db?.streamDao()?.deleteAndCreate(deleteAllStreams = false, updatedStreams)
                }
        }
        return streamsFromNetwork
            .publish { fromNetwork ->
                Observable.mergeDelayError(fromNetwork, streamsFromDB.takeUntil(fromNetwork))
                    .onErrorResumeWith(streamsFromDB)
            }
    }

    override fun getTopicsOfStreams(streams: List<Stream>): Observable<List<Stream>> {
        return Observable.fromIterable(streams)
            .concatMap { stream ->
                return@concatMap RetroClient.zulipApi.getTopics(stream.id)
                    .map { response ->
                        stream.topics = response.topics
                        return@map stream
                    }
            }
            .toList()
            .toObservable()

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

    override fun getMessages(
        nameOfTopic: String,
        nameOfStream: String,
        uidOfLastLoadedMessage: String,
        needFirstPage: Boolean
    ): Observable<List<Message>> {
        val narrow = NarrowConstructor.getNarrow(nameOfTopic, nameOfStream)
        val messagesFromDB = if (needFirstPage)
            Observable.fromCallable {
                db?.messageDao()?.getAll(nameOfStream, nameOfTopic) ?: emptyList()
            }
                .subscribeOn(Schedulers.io()) else Observable.empty()
        val messagesFromNetwork = RetroClient.zulipApi.getMessages(
            uidOfLastLoadedMessage,
            NUMBER_OF_MESSAGES_BEFORE,
            NUMBER_OF_MESSAGES_AFTER,
            narrow
        )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { messagesResponse ->
                val messages = messagesResponse.messages.map { message ->
                    message.nameOfStream = nameOfStream
                    message.nameOfTopic = nameOfTopic
                    message
                }
                if (needFirstPage) {
                    db?.messageDao()
                        ?.deleteAndCreate(nameOfStream, nameOfTopic, messages)
                } else {
                    val numberOfMessagesCanBeInserted =
                        MAX_NUMBER_OF_MESSAGES_IN_DB - (db?.messageDao()
                            ?.getCount(nameOfStream, nameOfTopic)
                            ?: MAX_NUMBER_OF_MESSAGES_IN_DB)
                    val messagesToDatabase = messages
                        .takeLast(numberOfMessagesCanBeInserted)
                    db?.messageDao()?.insertAll(messagesToDatabase)
                }
                return@map messages
            }
        return messagesFromNetwork
            .publish { fromNetwork ->
                Observable.mergeDelayError(fromNetwork, messagesFromDB.takeUntil(fromNetwork))
                    .onErrorResumeWith(messagesFromDB)
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
                    val viewTypedMessages = parseMessages(zulipMessages)
                    val newList = currentList.filter { it.uid !in queueOfRawUidsOfMessages }
                    val actualList = newList + viewTypedMessages
                    db?.messageDao()
                        ?.insertAllAndCheckCapacity(nameOfStream, nameOfTopic, zulipMessages)
                    return@map newLastEventId to actualList
                } else {
                    return@map lastEventId to emptyList<ViewTyped>()
                }
            }
            .onErrorComplete()
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
            .onErrorComplete()
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
        reactions: List<ReactionUI>,
        reactionEvent: ReactionEvent
    ): List<ReactionUI> {
        val targetReaction = ReactionUI(
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
                        reactions = parseReactions(messageToken.reactions),
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
                        reactions = parseReactions(messageToken.reactions),
                        dateInSeconds = messageToken.dateInSeconds,
                        avatarUrl = messageToken.avatarUrl,
                        uid = messageToken.id,
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
        zulipReactions: List<Reaction>,
    ): List<ReactionUI> {
        val listOfReactions = mutableListOf<ReactionUI>()
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
                listOfReactions.add(ReactionUI(emojiCode, 1, mutableListOf(userId)))
            }
        }
        return listOfReactions.reversed()
    }
}

