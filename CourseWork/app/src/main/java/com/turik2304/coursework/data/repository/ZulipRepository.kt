package com.turik2304.coursework.data.repository

import com.turik2304.coursework.data.MyUserId
import com.turik2304.coursework.data.network.ZulipApi
import com.turik2304.coursework.data.network.models.data.*
import com.turik2304.coursework.data.network.models.data.MessageData.MessageLongpollingData
import com.turik2304.coursework.data.network.models.data.MessageData.ReactionLongpollingData
import com.turik2304.coursework.data.network.models.response.GetOwnProfileResponse
import com.turik2304.coursework.data.network.models.response.GetUserPresenceResponse
import com.turik2304.coursework.data.network.models.response.RegisterEventsResponse
import com.turik2304.coursework.data.network.utils.NarrowConstructor
import com.turik2304.coursework.data.network.utils.ViewTypedConverter
import com.turik2304.coursework.data.room.Database
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class ZulipRepository(
    override val api: ZulipApi,
    override val db: Database,
    override val converter: ViewTypedConverter,
    override val narrowConstructor: NarrowConstructor
) : Repository {

    companion object {
        private const val NUMBER_OF_MESSAGES_BEFORE = 20
        private const val NUMBER_OF_MESSAGES_AFTER = 0
        private const val MAX_NUMBER_OF_MESSAGES_IN_DB = 50
    }

    override fun getAllUsers(): Observable<List<User>> {
        val usersFromDB =
            Observable.fromCallable {
                db.userDao().getAll()
            }
                .subscribeOn(Schedulers.io())
        val usersFromNetWork = api.getAllUsers()
            .subscribeOn(Schedulers.io())
            .flatMap { response ->
                val sortedUserList = response.members.sortedBy { user -> user.userName }
                Observable.fromIterable(sortedUserList)
                    .concatMap { user ->
                        return@concatMap if (user.isBot) {
                            Observable.just(user)
                        } else {
                            api.getUserPresence(user.email)
                                .map m@{ response ->
                                    user.presence = response.presence.aggregated.statusEnum
                                    return@m user
                                }
                        }
                    }
                    .toList()
                    .toObservable()
                    .doOnNext { updatedUserList ->
                        db.userDao().deleteAndCreate(updatedUserList)
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
            db.streamDao().getStreams(needAllStreams)
        }
            .subscribeOn(Schedulers.io())
        val streamsFromNetwork: Observable<List<Stream>>
        if (needAllStreams) {
            streamsFromNetwork = api.getAllStreams()
                .subscribeOn(Schedulers.io())
                .flatMap { allStreamsResponse ->
                    getTopicsOfStreams(allStreamsResponse.allStreams)
                }
                .doOnNext { updatedStreams ->
                    db.streamDao().deleteAndCreate(deleteAllStreams = true, updatedStreams)
                }
        } else {
            streamsFromNetwork = api.getSubscribedStreams()
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
                    db.streamDao().deleteAndCreate(deleteAllStreams = false, updatedStreams)
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
                return@concatMap api.getTopics(stream.id)
                    .map { response ->
                        stream.topics = response.topics.map topicNameMap@{ topic ->
                            topic.nameOfStream = stream.nameOfStream
                            topic.streamColor = stream.color
                            return@topicNameMap topic
                        }
                        return@map stream
                    }
            }
            .toList()
            .toObservable()
    }

    override fun getOwnProfile(): Observable<GetOwnProfileResponse> {
        val getOwnProfile = api.getOwnProfile()
        val getOwnPresence = api.getUserPresence(MyUserId.MY_USER_ID.toString())
        return Observable.zip(getOwnProfile, getOwnPresence,
            { ownProfileResponse: GetOwnProfileResponse, ownPresence: GetUserPresenceResponse ->
                ownProfileResponse.statusEnum = ownPresence.presence.aggregated.statusEnum
                return@zip ownProfileResponse
            })
            .subscribeOn(Schedulers.io())
    }

    override fun sendMessage(
        nameOfTopic: String,
        nameOfStream: String,
        message: String
    ): Observable<OutMessageUI> {
        //generates a raw message right now for display,
        //raw message will be updated when MessageEvent is receive
        val rawMessage = converter.messageHelper.generateRawMessage(message)
        val rawMessageSupplier = Observable.just(rawMessage)
        val sendMessageCall = api.sendMessage(
            nameOfTopic = nameOfTopic,
            nameOfStream = nameOfStream,
            message = message
        )
            .subscribeOn(Schedulers.io())
        return sendMessageCall.startWith(rawMessageSupplier)
    }

    override fun sendReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String
    ): Completable {
        return api.sendReaction(
            messageId = messageId,
            emojiName = emojiName,
            emojiCode = emojiCode
        )
            .subscribeOn(Schedulers.io())
    }

    override fun removeReaction(
        messageId: Int,
        emojiName: String,
        emojiCode: String
    ): Completable {
        return api.removeReaction(
            messageId = messageId,
            emojiName = emojiName,
            emojiCode = emojiCode
        )
    }

    override fun getMessages(
        nameOfTopic: String,
        nameOfStream: String,
        uidOfLastLoadedMessage: String,
        needFirstPage: Boolean
    ): Observable<List<Message>> {
        val narrow = narrowConstructor.getNarrow(nameOfTopic, nameOfStream)
        val messagesFromDB = if (needFirstPage)
            Observable.fromCallable {
                db.messageDao().getAll(nameOfStream, nameOfTopic)
            }
                .subscribeOn(Schedulers.io()) else Observable.empty()
        val messagesFromNetwork = api.getMessages(
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
                    db.messageDao()
                        .deleteAndCreate(nameOfStream, nameOfTopic, messages)
                } else {
                    val numberOfMessagesCanBeInserted =
                        MAX_NUMBER_OF_MESSAGES_IN_DB - db.messageDao()
                            .getCount(nameOfStream, nameOfTopic)
                    val messagesToDatabase = messages
                        .takeLast(numberOfMessagesCanBeInserted)
                    db.messageDao().insertAll(messagesToDatabase)
                }
                return@map messages
            }

        return messagesFromNetwork
            .publish { fromNetwork ->
                Observable.mergeDelayError(fromNetwork, messagesFromDB.takeUntil(fromNetwork))
                    .onErrorResumeWith(messagesFromDB)
            }
    }

    override fun registerEvents(
        nameOfTopic: String,
        nameOfStream: String
    ): Observable<MessageData.EventRegistrationData> {
        val narrow = narrowConstructor.getNarrowArray(nameOfTopic, nameOfStream)
        val registerMessageEvents = api.registerMessageEvents(narrow = narrow)
        val registerReactionEvents = api.registerReactionEvents(narrow = narrow)
        return Observable.zip(registerMessageEvents, registerReactionEvents,
            { registerMessageEventsResponse: RegisterEventsResponse, registerReactionEventsResponse: RegisterEventsResponse ->
                converter.messageHelper.setOfRawIdsOfMessages.clear()
                return@zip MessageData.EventRegistrationData(
                    messagesQueueId = registerMessageEventsResponse.queueId,
                    messageEventId = registerMessageEventsResponse.lastEventId,
                    reactionsQueueId = registerReactionEventsResponse.queueId,
                    reactionEventId = registerReactionEventsResponse.lastEventId
                )
            })
            .subscribeOn(Schedulers.io())
    }

    override fun updateMessagesByMessageEvent(
        queueId: String,
        lastEventId: String,
        nameOfTopic: String,
        nameOfStream: String,
        currentList: List<ViewTyped>,
    ): Observable<MessageLongpollingData> {
        return api.getMessageEvents(queueId, lastEventId)
            .subscribeOn(Schedulers.io())
            .map { response ->
                if (response.messageEvents.isNotEmpty() && currentList.isNotEmpty()) {
                    val newLastEventId = response.messageEvents.last().id
                    val zulipMessages = response.messageEvents.map { messageEvent ->
                        val message = messageEvent.message
                        message.nameOfTopic = nameOfTopic
                        message.nameOfStream = nameOfStream
                        message
                    }
                    val newMessages = converter.messageHelper.parseMessages(zulipMessages)
                    //current list may include raw messages
                    val filteredMessages = converter.messageHelper.filterRawMessages(currentList)
                    val actualList = filteredMessages + newMessages
                    db.messageDao()
                        .insertAllAndCheckCapacity(nameOfStream, nameOfTopic, zulipMessages)
                    return@map MessageLongpollingData(
                        messagesQueueId = queueId,
                        lastMessageEventId = newLastEventId,
                        polledData = actualList
                    )
                } else {
                    return@map MessageLongpollingData(
                        messagesQueueId = queueId,
                        lastMessageEventId = lastEventId,
                        polledData = emptyList()
                    )
                }
            }
            .retry()
    }

    override fun updateMessagesByReactionEvent(
        queueId: String,
        lastEventId: String,
        currentList: List<ViewTyped>,
    ): Observable<ReactionLongpollingData> {
        return api.getReactionEvents(queueId, lastEventId)
            .subscribeOn(Schedulers.io())
            .map { response ->
                if (response.reactionEvents.isNotEmpty() && currentList.isNotEmpty()) {
                    val newLastEventId = response.reactionEvents.last().id
                    val reactionEvents = response.reactionEvents
                    val updatedList = converter.messageHelper
                        .reactionHelper.updateReactions(currentList, reactionEvents)
                    return@map ReactionLongpollingData(
                        reactionsQueueId = queueId,
                        lastReactionEventId = newLastEventId,
                        polledData = updatedList
                    )
                } else return@map ReactionLongpollingData(
                    reactionsQueueId = queueId,
                    lastReactionEventId = lastEventId,
                    polledData = emptyList()
                )
            }
            .retry()
    }

    override fun updateMessagesByEvents(
        nameOfTopic: String,
        nameOfStream: String,
        messageQueueId: String,
        messageEventId: String,
        reactionQueueId: String,
        reactionEventId: String,
        currentList: List<ViewTyped>,
    ): Observable<MessageData> {
        val messagesEvents = updateMessagesByMessageEvent(
            queueId = messageQueueId,
            lastEventId = messageEventId,
            nameOfTopic = nameOfTopic,
            nameOfStream = nameOfStream,
            currentList = currentList,
        )
        val reactionsEvents = updateMessagesByReactionEvent(
            queueId = reactionQueueId,
            lastEventId = reactionEventId,
            currentList = currentList
        )
        return Observable.mergeDelayError(messagesEvents, reactionsEvents)
            .first(MessageData.EmptyLongpollingData)
            .toObservable()
    }
}

