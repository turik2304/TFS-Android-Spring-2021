package com.turik2304.coursework.network

import com.turik2304.coursework.MyUserId
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.taliox.zulip.ZulipRestExecutor
import io.taliox.zulip.calls.messages.AddReaction
import io.taliox.zulip.calls.messages.DeleteReaction
import io.taliox.zulip.calls.messages.GetMessages
import io.taliox.zulip.calls.messages.PostMessage
import io.taliox.zulip.calls.streams.GetAllStreams
import io.taliox.zulip.calls.streams.GetAllTopicsOfAStream
import io.taliox.zulip.calls.streams.GetSubscribedStreams
import io.taliox.zulip.calls.users.GetAllUsers
import io.taliox.zulip.calls.users.GetProfile
import io.taliox.zulip.calls.users.GetUserPresence
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FakeServerApi : ServerApi {

    override val userName: String
        //generate random errors
//        get() = if (Random().nextBoolean()) "asibag98@gmail.com" else "bobob"
        get() = "asibag98@gmail.com"
    override val password: String
        get() = "fjMrYYPpJBw87hculEvh47Ckc7eW08yN"
    override val serverURL: String
        get() = "https://tfs-android-2021-spring.zulipchat.com/"

    override fun getStreamUIListFromServer(needAllStreams: Boolean): Single<List<ViewTyped>> {
        val key: String
        val getStreams = if (needAllStreams) {
            key = "streams"
            GetAllStreams()
        } else {
            key = "subscriptions"
            GetSubscribedStreams()
        }
        val executor = ZulipRestExecutor(
            userName, password, serverURL
        )
        return Single.fromCallable { getStreams.execute(executor) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { response -> JSONObject(response) }
            .map { jsonObject -> jsonObject.getJSONArray(key) }
            .map { jsonArrayOfStreams ->
                val listOfStreams = mutableListOf<ViewTyped>()
                for (indexOfStream in 0 until jsonArrayOfStreams.length()) {
                    val jsonObjectStream = jsonArrayOfStreams.get(indexOfStream) as JSONObject
                    val nameOfStream = jsonObjectStream.get("name").toString()
                    val uid = jsonObjectStream.get("stream_id").toString()
                    listOfStreams.add(StreamUI(nameOfStream, uid))
                    listOfStreams.add(StreamAndTopicSeparatorUI(uid = "STREAM_SEPARATOR_$uid"))
                }
                return@map listOfStreams
            }
    }

    override fun getTopicsUIListByStreamUid(streamUid: String): Single<List<ViewTyped>> {
        val getTopicsOfStream = GetAllTopicsOfAStream(streamUid)
        val executor = ZulipRestExecutor(
            userName, password, serverURL
        )
        return Single.fromCallable { getTopicsOfStream.execute(executor) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { response -> JSONObject(response) }
            .map { jsonObject ->
                val jsonArrayOfTopics = jsonObject.getJSONArray("topics")
                val listOfTopics = mutableListOf<ViewTyped>()
                for (indexOfTopic in 0 until jsonArrayOfTopics.length()) {
                    val jsonObjectTopic = jsonArrayOfTopics.get(indexOfTopic) as JSONObject
                    val nameOfTopic = jsonObjectTopic.get("name").toString()
                    val uid = jsonObjectTopic.get("max_id").toString()
                    listOfTopics.add(TopicUI(name = nameOfTopic, uid = uid))
                    listOfTopics.add(
                        StreamAndTopicSeparatorUI(
                            uid = "TOPIC_SEPARATOR_${uid}"
                        )
                    )
                }
                return@map listOfTopics
            }
    }

    override fun getMessageUIListFromServer(
        nameOfTopic: String,
        nameOfStream: String
    ): Single<List<ViewTyped>> {
        val executor = ZulipRestExecutor(
            userName, password, serverURL
        )
        val getMessages = GetMessages(100, 0)
        val operand = "\"operand\""
        val operator = "\"operator\""
        val streamKey = "\"stream\""
        val topicKey = "\"topic\""
        val jsonNameOfTopic = "\"$nameOfTopic\""
        val jsonNameOfStream = "\"$nameOfStream\""
        val narrow = "[{$operand: $jsonNameOfStream, $operator: $streamKey}," +
                "{$operand: $jsonNameOfTopic, $operator: $topicKey}]"
        getMessages.narrow = narrow
        return Single
            .fromCallable { getMessages.execute(executor) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { message -> JSONObject(message.toString()) }
            .map { jsonObject -> jsonObject.getJSONArray("messages") }
            .map { jsonArray ->
                val listOfMessages = mutableListOf<ServerApi.Message>()
                for (indexOfMessage in 0 until jsonArray.length()) {
                    val jsonObjectMessage = jsonArray.get(indexOfMessage) as JSONObject
                    val uid = jsonObjectMessage.get("id").toString()
                    val userName = jsonObjectMessage.get("sender_full_name").toString()
                    val dateInSeconds =
                        Integer.parseInt(jsonObjectMessage.get("timestamp").toString())
                    val senderId = jsonObjectMessage.get("sender_id").toString()
                    val message = jsonObjectMessage.get("content").toString()
                    val arrayOfReactions = jsonObjectMessage.getJSONArray("reactions")
                    val reactions = parseReactions(arrayOfReactions)
                    listOfMessages.add(
                        ServerApi.Message(
                            userName,
                            message,
                            dateInSeconds,
                            senderId,
                            reactions,
                            uid
                        )
                    )

                }
                return@map listOfMessages
            }
            .map { messageList ->
                messageList
                    .sortedBy { it.dateInSeconds }
                    .groupBy { message ->
                        getFormattedDate(message.dateInSeconds)
                    }
                    .flatMap { (date, messages) ->
                        listOf(DateSeparatorUI(date, "DATE_SEPARATOR_$date")) + parseMessages(
                            messages
                        )
                    }
            }
    }

    private fun parseMessages(remoteMessages: List<ServerApi.Message>): List<ViewTyped> {
        val messageUIList = mutableListOf<ViewTyped>()
        remoteMessages.forEach { messageToken ->
            if (messageToken.userId == MyUserId.MY_USER_ID) {
                messageUIList.add(
                    OutMessageUI(
                        userName = messageToken.userName,
                        userId = messageToken.userId,
                        message = messageToken.message,
                        reactions = messageToken.reactions,
                        dateInSeconds = messageToken.dateInSeconds,
                        uid = messageToken.uid,
                    )
                )
            } else {
                messageUIList.add(
                    InMessageUI(
                        userName = messageToken.userName,
                        userId = messageToken.userId,
                        message = messageToken.message,
                        reactions = messageToken.reactions,
                        dateInSeconds = messageToken.dateInSeconds,
                        uid = messageToken.uid,
                    )
                )
            }
        }
        return messageUIList
    }

    private fun getFormattedDate(dateOfMessageInSeconds: Int): String {
        val formatter = SimpleDateFormat("dd MMMM")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateOfMessageInSeconds * 1000L
        return formatter.format(calendar.time)
    }

    private fun parseReactions(jsonArrayOfReactions: JSONArray): List<ServerApi.Reaction> {
        val listOfReactions = mutableListOf<ServerApi.Reaction>()
        for (indexOfReaction in 0 until jsonArrayOfReactions.length()) {
            val jsonObjectReaction = jsonArrayOfReactions.get(indexOfReaction) as JSONObject
            val emojiType = jsonObjectReaction.get("reaction_type").toString()
            if (emojiType == "unicode_emoji") {
                val emojiCodeString = jsonObjectReaction.get("emoji_code").toString()
                val emojiCode = Integer.parseInt(emojiCodeString, 16)
                val userId = jsonObjectReaction.get("user_id").toString()
                var isTheSameReaction = false
                var indexOfSameReaction = -1
                listOfReactions.forEachIndexed { index, reaction ->
                    if (reaction.emojiCode == emojiCode) {
                        isTheSameReaction = true
                        indexOfSameReaction = index
                    }
                }
                if (isTheSameReaction) {
                    listOfReactions[indexOfSameReaction].counter++
                    listOfReactions[indexOfSameReaction].usersWhoClicked.add(userId)
                } else {
                    listOfReactions.add(ServerApi.Reaction(emojiCode, 1, mutableListOf(userId)))
                }
            }
        }
        return listOfReactions
    }

    override fun sendMessageToServer(
        nameOfTopic: String,
        nameOfStream: String,
        message: String
    ): Completable {
        val executor = ZulipRestExecutor(
            userName, password, serverURL
        )
        val postMessage = PostMessage(
            nameOfStream,
            nameOfTopic,
            message
        )
        return Completable
            .fromCallable { executor.executeCall(postMessage) }
            .subscribeOn(Schedulers.io())
    }

    override fun sendReaction(
        uidOfMessage: String,
        emojiCode: String,
        emojiName: String
    ): Completable {
        val addReaction = AddReaction(uidOfMessage, emojiCode, emojiName)
        val executor = ZulipRestExecutor(
            userName, password, serverURL
        )
        return Completable.fromCallable { addReaction.execute(executor) }
            .subscribeOn(Schedulers.io())
    }

    override fun removeReaction(
        uidOfMessage: String,
        emojiCode: String,
        emojiName: String
    ): Completable {
        val deleteReaction = DeleteReaction(uidOfMessage, emojiCode, emojiName)
        val executor = ZulipRestExecutor(
            userName, password, serverURL
        )
        return Completable.fromCallable { deleteReaction.execute(executor) }
            .subscribeOn(Schedulers.io())
    }

    override fun getProfileDetailsById(email: String): Single<String> {
        val executor = ZulipRestExecutor(
            userName, password, serverURL
        )
        val getUserPresence = GetUserPresence(email)
        return Single.fromCallable { getUserPresence.execute(executor) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { response -> JSONObject(response) }
            .map { jsonObject -> jsonObject.get("presence") as JSONObject }
            .map { jsonObjectPresence -> jsonObjectPresence.get("aggregated") as JSONObject }
            .map { jsonObjectAggregated -> jsonObjectAggregated.getString("status") }
    }

    override fun getUserUIListFromServer(): Single<List<ViewTyped>> {
        val executor = ZulipRestExecutor(
            userName, password, serverURL
        )
        val getAllUsers = GetAllUsers()
        return Single.fromCallable { getAllUsers.execute(executor) }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .map { response -> JSONObject(response) }
            .map { jsonObject -> jsonObject.getJSONArray("members") }
            .map { jsonArrayOfUsers ->
                val listOfUser = mutableListOf<ViewTyped>()
                for (indexOfUser in 0 until jsonArrayOfUsers.length()) {
                    val jsonObjectUser = jsonArrayOfUsers.get(indexOfUser) as JSONObject
                    val email = jsonObjectUser.get("email").toString()
                    val name = jsonObjectUser.get("full_name").toString()
                    val uid = jsonObjectUser.get("user_id").toString()
                    listOfUser.add(
                        UserUI(
                            userName = name,
                            email = email,
                            uid = uid
                        )
                    )
                }
                return@map listOfUser
            }
    }

    override fun getOwnProfile(): Single<Map<String, String>> {
        val executor = ZulipRestExecutor(
            userName, password, serverURL
        )
        val getOwnProfile = GetProfile()
        return Single.fromCallable { getOwnProfile.execute(executor) }
            .subscribeOn(Schedulers.io())
            .map { respone -> JSONObject(respone) }
            .map { jsonObjectResponse ->
                val userName = jsonObjectResponse.get("full_name").toString()
                return@map mapOf("userName" to userName)
            }
    }


}
