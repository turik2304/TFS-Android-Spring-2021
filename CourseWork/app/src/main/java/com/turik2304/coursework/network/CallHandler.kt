package com.turik2304.coursework.network

import androidx.fragment.app.FragmentActivity
import com.turik2304.coursework.recycler_view_base.ViewTyped
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface CallHandler {

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

    val userName: String
    val password: String
    val serverURL: String

    fun getStreamUIListFromServer(
        needAllStreams: Boolean,
        activity: FragmentActivity,
        loaderId: Int
    ): Observable<MutableList<ViewTyped>>

    fun getTopicsUIListByStreamUid(
        streamUid: String,
        activity: FragmentActivity
    ): Observable<MutableList<ViewTyped>>

    fun getMessageUIListFromServer(
        nameOfTopic: String,
        nameOfStream: String,
        activity: FragmentActivity,
        loaderId: Int
    ): Observable<List<ViewTyped>>

    fun getProfileDetailsById(
        email: String,
        activity: FragmentActivity
    ): Observable<String>

    fun getUserUIListFromServer(
        activity: FragmentActivity,
        loaderId: Int
    ): Observable<MutableList<ViewTyped>>

    fun getOwnProfile(
        activity: FragmentActivity,
    ): Observable<Map<String, String>>

    fun sendMessageToServer(nameOfTopic: String, nameOfStream: String, message: String): Completable
    fun sendReaction(uidOfMessage: String, emojiCode: String, emojiName: String): Completable
    fun removeReaction(uidOfMessage: String, emojiCode: String, emojiName: String): Completable
}