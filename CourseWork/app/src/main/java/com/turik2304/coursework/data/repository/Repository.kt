package com.turik2304.coursework.data.repository

import com.turik2304.coursework.data.network.ZulipApi
import com.turik2304.coursework.data.network.models.data.*
import com.turik2304.coursework.data.network.models.response.GetOwnProfileResponse
import com.turik2304.coursework.data.network.utils.NarrowConstructor
import com.turik2304.coursework.data.network.utils.ViewTypedConverter
import com.turik2304.coursework.data.room.Database
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.items.OutMessageUI
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface Repository {
    val api: ZulipApi
    val db: Database
    val converter: ViewTypedConverter
    val narrowConstructor: NarrowConstructor

    fun getStreams(needAllStreams: Boolean): Observable<List<Stream>>
    fun getTopicsOfStreams(streams: List<Stream>): Observable<List<Stream>>
    fun getMessages(
        nameOfTopic: String,
        nameOfStream: String,
        uidOfLastLoadedMessage: String,
        needFirstPage: Boolean = false
    ): Observable<List<Message>>

    fun getOwnProfile(): Observable<GetOwnProfileResponse>
    fun updateMessagesByReactionEvent(
        queueId: String,
        lastEventId: String,
        currentList: List<ViewTyped>
    ): Observable<MessageData.ReactionLongpollingData>

    fun updateMessagesByMessageEvent(
        queueId: String,
        lastEventId: String,
        nameOfTopic: String,
        nameOfStream: String,
        currentList: List<ViewTyped>,
    ): Observable<MessageData.MessageLongpollingData>

    fun getAllUsers(): Observable<List<User>>

    fun updateMessagesByEvents(
        nameOfTopic: String,
        nameOfStream: String,
        messageQueueId: String,
        messageEventId: String,
        reactionQueueId: String,
        reactionEventId: String,
        currentList: List<ViewTyped>,
    ): Observable<MessageData>

    fun registerEvents(
        nameOfTopic: String,
        nameOfStream: String
    ): Observable<MessageData.EventRegistrationData>

    fun sendMessage(
        nameOfTopic: String,
        nameOfStream: String,
        message: String
    ): Observable<OutMessageUI>

    fun getBottomSheetReactionList(): Observable<List<BottomSheetReaction>>
    fun sendReaction(messageId: Int, emojiName: String, emojiCode: String): Completable
    fun removeReaction(messageId: Int, emojiName: String, emojiCode: String): Completable
}