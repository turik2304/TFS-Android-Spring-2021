package com.turik2304.coursework.network

import com.turik2304.coursework.recycler_view_base.ViewTyped
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface ServerApi {
    data class User(
        val uid: String,
        val userName: String,
        val email: String,
        val statusText: String,
        val status: String
    )

    data class Reaction(
        val emojiCode: Int,
        var counter: Int,
        val usersWhoClicked: MutableList<String>
    )

    data class Message(
        val userName: String,
        val message: String,
        val dateInSeconds: Int,
        val userId: String,
        val reactions: List<Reaction>,
        val uid: String
    )

    val userList: List<User>
    fun getProfileDetailsById(uid: String): Map<String, String>

    val userName: String
    val password: String
    val serverURL: String
    fun getStreamUIListFromServer(needAllStreams: Boolean): Single<List<ViewTyped>>
    fun getTopicsUIListByStreamUid(streamUid: String): Single<List<ViewTyped>>
    fun getMessageUIListFromServer(
        nameOfTopic: String,
        nameOfStream: String
    ): Single<List<ViewTyped>>

    fun sendMessageToServer(nameOfTopic: String, nameOfStream: String, message: String): Completable
    fun sendReaction(uidOfMessage: String, emojiCode: String, emojiName: String): Completable
    fun removeReaction(uidOfMessage: String, emojiCode: String, emojiName: String): Completable
}