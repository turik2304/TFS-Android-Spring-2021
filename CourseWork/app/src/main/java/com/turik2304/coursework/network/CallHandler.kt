package com.turik2304.coursework.network

import com.turik2304.coursework.network.calls.ZulipMessage
import com.turik2304.coursework.recycler_view_base.ViewTyped
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface CallHandler {

    data class Reaction(
            val emojiCode: Int,
            var counter: Int,
            val usersWhoClicked: MutableList<Int>
    )

    fun getStreamUIListFromServer(needAllStreams: Boolean): Single<MutableList<ViewTyped>>
    fun getTopicsUIListByStreamUid(streamUid: Int): Single<MutableList<ViewTyped>>
    fun getMessageUIListFromServer(
            nameOfTopic: String,
            nameOfStream: String
    ): Single<List<ViewTyped>>

    fun getOwnProfile(): Single<Map<String, String>>
    fun parseMessages(remoteMessages: List<ZulipMessage>): List<ViewTyped>
}