package com.turik2304.coursework.network

import com.turik2304.coursework.recycler_view_base.ViewTyped
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface CallHandler {

    data class Reaction(
        val emojiCode: Int,
        var counter: Int,
        val usersWhoClicked: MutableList<Int>
    )

    fun getStreamUIListFromServer(needAllStreams: Boolean): Single<MutableList<ViewTyped>>
    fun getTopicsUIListByStreamUid(streamUid: Int): Single<MutableList<ViewTyped>>
    fun List<ViewTyped>.addSeparators(): MutableList<ViewTyped>
    fun getMessageUIListFromServer(
        nameOfTopic: String,
        nameOfStream: String
    ): Single<List<ViewTyped>>
}