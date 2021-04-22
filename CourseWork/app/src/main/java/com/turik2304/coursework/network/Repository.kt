package com.turik2304.coursework.network

import com.turik2304.coursework.network.models.data.ReactionEvent
import com.turik2304.coursework.network.models.data.StatusEnum
import com.turik2304.coursework.network.models.response.GetOwnProfileResponse
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.StreamUI
import com.turik2304.coursework.recycler_view_base.items.TopicUI
import com.turik2304.coursework.recycler_view_base.items.UserUI
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface Repository {

    fun getStreamUIListFromServer(needAllStreams: Boolean): Single<List<StreamUI>>
    fun updateTopicsOfStream(stream: StreamUI): Single<List<TopicUI>>
    fun getMessageUIListFromServer(
            nameOfTopic: String,
            nameOfStream: String,
            uidOfLastLoadedMessage: String,
            needOneMessage: Boolean,
            isFirstLoad: Boolean = false
    ): Single<List<ViewTyped>>

    fun updateMessageUIListAfterSendingMessage(
            nameOfTopic: String,
            nameOfStream: String,
            uidOfSentMessage: String,
            currentList: MutableList<ViewTyped>
    ): Single<List<ViewTyped>>

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

    fun updateUserPresence(user: UserUI): Single<StatusEnum>
    fun getAllUsers(): Single<List<UserUI>>

}