package com.turik2304.coursework.network

import com.turik2304.coursework.network.models.data.ReactionEvent
import com.turik2304.coursework.network.models.response.GetOwnProfileResponse
import com.turik2304.coursework.network.models.response.ResponseType
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.StreamUI
import com.turik2304.coursework.recycler_view_base.items.UserUI
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface Repository {

    fun getStreams(needAllStreams: Boolean): Observable<List<StreamUI>>
    fun getTopicsOfStreams(streams: List<StreamUI>): Observable<List<StreamUI>>
    fun getMessages(
        nameOfTopic: String,
        nameOfStream: String,
        uidOfLastLoadedMessage: String,
        isFirstLoad: Boolean = false
    ): Observable<List<ViewTyped>>

    fun getOwnProfile(): Single<GetOwnProfileResponse>
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

    fun updateUserPresence(user: UserUI): Single<UserUI>
    fun getAllUsers(): Observable<Pair<List<UserUI>, ResponseType>>

}
