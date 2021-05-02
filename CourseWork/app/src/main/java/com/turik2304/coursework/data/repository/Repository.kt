package com.turik2304.coursework.data.repository

import com.turik2304.coursework.data.network.models.RemoteModel
import com.turik2304.coursework.data.network.models.data.Message
import com.turik2304.coursework.data.network.models.data.ReactionEvent
import com.turik2304.coursework.data.network.models.data.Stream
import com.turik2304.coursework.data.network.models.data.User
import com.turik2304.coursework.data.network.models.response.GetOwnProfileResponse
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface Repository {

    fun getStreams(needAllStreams: Boolean): Observable<List<Stream>>
    fun getTopicsOfStreams(streams: List<Stream>): Observable<List<Stream>>
    fun getMessages(
        nameOfTopic: String,
        nameOfStream: String,
        uidOfLastLoadedMessage: String,
        needFirstPage: Boolean = false
    ): Observable<List<Message>>

    fun getOwnProfile(): Observable<GetOwnProfileResponse>
    fun getFormattedDate(dateOfMessageInSeconds: Int): String
    fun updateReactions(
        currentList: List<ViewTyped>,
        reactionEvents: List<ReactionEvent>
    ): List<ViewTyped>

    fun getReactionEvent(
        queueId: String,
        lastEventId: String,
        currentList: List<ViewTyped>
    ): Observable<Pair<String, List<ViewTyped>>>

    fun getMessageEvent(
        queueId: String,
        lastEventId: String,
        nameOfTopic: String,
        nameOfStream: String,
        currentList: List<ViewTyped>,
        queueOfRawUidsOfMessages: HashSet<Int>
    ): Observable<Pair<String, List<ViewTyped>>>

    fun getAllUsers(): Observable<List<User>>

    fun <T : RemoteModel> Observable<List<T>>.toViewTypedItems(): Observable<List<ViewTyped>>
}