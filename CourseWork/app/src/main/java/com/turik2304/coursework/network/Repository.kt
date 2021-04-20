package com.turik2304.coursework.network

import com.turik2304.coursework.network.models.data.ZulipMessage
import com.turik2304.coursework.network.models.response.GetOwnProfileResponse
import com.turik2304.coursework.recycler_view_base.ViewTyped
import io.reactivex.rxjava3.core.Single

interface Repository {

    fun getStreamUIListFromServer(needAllStreams: Boolean): Single<MutableList<ViewTyped>>
    fun getTopicsUIListByStreamUid(streamUid: Int): Single<MutableList<ViewTyped>>
    fun getMessageUIListFromServer(
        nameOfTopic: String,
        nameOfStream: String
    ): Single<List<ViewTyped>>

    fun getOwnProfile(): Single<GetOwnProfileResponse>
    fun parseMessages(remoteMessages: List<ZulipMessage>): List<ViewTyped>

}