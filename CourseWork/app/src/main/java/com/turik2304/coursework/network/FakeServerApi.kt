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
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FakeServerApi : ServerApi {

    override val userName: String
        //generate random errors
        get() = if (Random().nextBoolean()) "asibag98@gmail.com" else "bobob"
    override val password: String
        get() = "fjMrYYPpJBw87hculEvh47Ckc7eW08yN"
    override val serverURL: String
        get() = "https://tfs-android-2021-spring.zulipchat.com/"

    override val userList = listOf(
        ServerApi.User(
            "ARTUR",
            "Artur Sibagatullin",
            "Sibagatullin@gmail.com",
            "In a meeting",
            "online"
        ),
        ServerApi.User(
            "Ivan_0",
            "Ivan Ivanov_0",
            "Ivan Ivanov_0@gmail.com",
            "In a meeting0",
            "offline"
        ),
        ServerApi.User(
            "Ivan_1",
            "Ivan Ivanov_1",
            "Ivan Ivanov_1@gmail.com",
            "In a meeting1",
            "online"
        ),
        ServerApi.User(
            "Ivan_2",
            "Ivan Ivanov_2",
            "Ivan Ivanov_2@gmail.com",
            "In a meeting2",
            "offline"
        ),
        ServerApi.User(
            "Ivan_3",
            "Ivan Ivanov_3",
            "Ivan Ivanov_3@gmail.com",
            "In a meeting3",
            "online"
        ),
        ServerApi.User(
            "Ivan_4",
            "Ivan Ivanov_4",
            "Ivan Ivanov_4@gmail.com",
            "In a meeting4",
            "offline"
        ),
        ServerApi.User(
            "Ivan_5",
            "Ivan Ivanov_5",
            "Ivan Ivanov_5@gmail.com",
            "In a meeting5",
            "online"
        ),
        ServerApi.User(
            "Ivan_6",
            "Ivan Ivanov_6",
            "Ivan Ivanov_6@gmail.com",
            "In a meetin6",
            "offline"
        ),
        ServerApi.User(
            "Ivan_7",
            "Ivan Ivanov_7",
            "Ivan Ivanov_7@gmail.com",
            "In a meeting7",
            "online"
        ),
        ServerApi.User(
            "Ivan_8",
            "Ivan Ivanov_8",
            "Ivan Ivanov_8@gmail.com",
            "In a meeting8",
            "offline"
        ),
        ServerApi.User(
            "Ivan_9",
            "Ivan Ivanov_9",
            "Ivan Ivanov_9@gmail.com",
            "In a meeting9",
            "online"
        ),
        ServerApi.User(
            "Ivan_10",
            "Ivan Ivanov_10",
            "Ivan Ivanov_10@gmail.com",
            "In a meeting10",
            "offline"
        ),
        ServerApi.User(
            "Ivan_11",
            "Ivan Ivanov_11",
            "Ivan Ivanov_10@gmail.com",
            "In a meeting11",
            "online"
        ),
        ServerApi.User(
            "Ivan_12",
            "Ivan Ivanov_12",
            "Ivan Ivanov_12@gmail.com",
            "In a meeting12",
            "offline"
        ),
        ServerApi.User(
            "Ivan_13",
            "Ivan Ivanov_13",
            "Ivan Ivanov_13@gmail.com",
            "In a meeting13",
            "online"
        ),
        ServerApi.User(
            "Ivan_14",
            "Ivan Ivanov_14",
            "Ivan Ivanov_14@gmail.com",
            "In a meeting14",
            "offline"
        ),
        ServerApi.User(
            "Ivan_15",
            "Ivan Ivanov_15",
            "Ivan Ivanov_15@gmail.com",
            "In a meeting15",
            "online"
        ),
        ServerApi.User(
            "Ivan_16",
            "Ivan Ivanov_16",
            "Ivan Ivanov_16@gmail.com",
            "In a meeting16",
            "offline"
        ),
    )

    override fun getProfileDetailsById(uid: String): Map<String, String> {
        val user = userList.find { user ->
            user.uid == uid
        }
        return mapOf(
            "userName" to (user?.userName ?: "none"),
            "statusText" to (user?.statusText ?: "none"),
            "status" to (user?.status ?: "none"),
        )
    }

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


}
