package com.turik2304.coursework.network

import com.turik2304.coursework.recycler_view_base.ViewTyped
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
        val counter: Int,
        val usersWhoClicked: List<String>
    )

    data class Message(
        val message: String,
        val dateInMillis: Long,
        val userId: String,
        val reactions: List<Reaction>,
        val uid: String
    )

    data class Topic(
        val name: String,
        val numberOfMessages: Int,
        val uid: String
    )

    val userList: List<User>
    val topicsByStreamUid: Map<String, List<Topic>>
    val subscribedStreamsWithUid: Map<String, String>
    val allStreams: Map<String, String>

    fun getUserNameById(uid: String): String
    fun sendMessages(listOfMessages: List<Message>)
    fun getMessages(): List<Message>
    fun getProfileDetailsById(uid: String): Map<String, String>

    val userName: String
    val password: String
    val serverURL: String
    fun getStreamUIListFromServer(needAllStreams: Boolean): Single<List<ViewTyped>>
    fun getTopicsUIListByStreamUid(streamUid: String): Single<List<ViewTyped>>
}