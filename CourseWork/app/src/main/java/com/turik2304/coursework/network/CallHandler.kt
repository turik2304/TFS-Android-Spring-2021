package com.turik2304.coursework.network

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.InMessageUI
import com.turik2304.coursework.recycler_view_base.items.StreamUI
import com.turik2304.coursework.recycler_view_base.items.TopicUI
import io.reactivex.rxjava3.core.Single
import kotlinx.serialization.Serializable

interface CallHandler {

    @Entity(tableName = "reactions",
            foreignKeys = [ForeignKey(entity = InMessageUI::class,
                    parentColumns = arrayOf("uid"),
                    childColumns = arrayOf("uidOfMessage"),
                    onDelete = ForeignKey.CASCADE)])
    @Serializable
    data class Reaction(
            @PrimaryKey
            val emojiCode: Int,
            var counter: Int,
            val usersWhoClicked: MutableList<Int>,
            val uidOfMessage: Int
    )

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

    fun getOwnProfile(): Single<Pair<String, String>>
    fun getFormattedDate(dateOfMessageInSeconds: Int): String
}