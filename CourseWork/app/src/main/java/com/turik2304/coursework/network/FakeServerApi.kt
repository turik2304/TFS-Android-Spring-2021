package com.turik2304.coursework.network

import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.StreamAndTopicSeparatorUI
import com.turik2304.coursework.recycler_view_base.items.StreamUI
import com.turik2304.coursework.recycler_view_base.items.TopicUI
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.taliox.zulip.ZulipRestExecutor
import io.taliox.zulip.calls.streams.GetAllStreams
import io.taliox.zulip.calls.streams.GetAllTopicsOfAStream
import io.taliox.zulip.calls.streams.GetSubscribedStreams
import org.json.JSONObject
import java.util.*

class FakeServerApi : ServerApi {

    override val userName: String
        //generate random errors
        get() =  if (Random().nextBoolean()) "asibag98@gmail.com" else "bobob"
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

    private val react0 = ServerApi.Reaction(0x1F600, 1, listOf("ARTUR"))
    private val react1 = ServerApi.Reaction(0x1F601, 2, listOf("ARTUR", "DENIS"))
    private val react2 = ServerApi.Reaction(0x1F602, 3, listOf("IVAN", "ROMA", "ALEX"))
    private val react3 = ServerApi.Reaction(0x1F603, 4, listOf("ANDREI", "ARTUR", "ROMA", "ALEX"))

    private var messages = listOf(
        ServerApi.Message(
            "– Буддлея! – с досадой проговорил он, прочитав текст сообщения.",
            1615628355222, "Ivan_0", listOf(react0), "1"
        ),
        ServerApi.Message(
            "Наконец, для большей эффективности в тексте сообщения следует максимально использовать все возможные элементы",
            1615628356222, "Ivan_1", listOf(react0), "2"
        ),
        ServerApi.Message(
            "Он бегло просмотрел тексты сообщений, отобранных и систематизированных для него кибернетической системой логической обработки данных",
            1615728355222, "Ivan_2", listOf(react1), "3"
        ),
        ServerApi.Message(
            "ТЕКСТ, -а, м. 1. Слова, предложения в определенной связи и последовательности, образующие какое-л. высказывание, сочинение,",
            1615728356222, "Ivan_3", listOf(react2), "4"
        ),
        ServerApi.Message(
            "Текст воинской присяги. Текст пьесы. Записать текст сказки. (Малый академический словарь, МАС)",
            1615728357222, "Ivan_4", listOf(react3), "5"
        ),
        ServerApi.Message(
            "Привет! Меня зовут Лампобот, я компьютерная программа, которая помогает делать Карту слов",
            1615728358222, "Ivan_5", listOf(react1, react2), "6"
        ),
        ServerApi.Message(
            "Действие по знач. глаг. сообщить—сообщать и сообщиться—сообщаться. ",
            1615728359222, "Ivan_6", listOf(react3), "7"
        ),
        ServerApi.Message(
            "Осенью 1913 года весь мир облетело сенсационное сообщение об открытии русскими моряками неизвестных земель",
            1615828360222, "Ivan_7", listOf(react0), "8"
        ),
        ServerApi.Message(
            "По нехоженой земле. О расстреле мирной манифестации рабочих у Зимнего дворца первым принес сообщение Антон Топилкин. Марков, Строговы",
            1615828361222, "Ivan_8", listOf(react2), "9"
        ),
        ServerApi.Message(
            "Данные, сведения, передаваемые, сообщаемые, излагаемые кем-л. Сообщение бюро погоды.",
            1615828355222, "Ivan_9", listOf(react1), "10"
        ),
        ServerApi.Message(
            "Офицер доложил последнее сообщение рации; за исключением тридцать седьмой, размещение корпуса закончилось. ",
            1615928356222, "Ivan_10", listOf(react1), "11"
        ),
        ServerApi.Message(
            "Леонов, Взятие Великошумска. ",
            1615928357222, "Ivan_11", listOf(react2, react3, react1), "12"
        ),
        ServerApi.Message(
            "Небольшой доклад на какую-л. тему, информация. Котельников поехал в институт, к профессору Карелину, делать какое-то сообщение на кафедре. ",
            1615928358222, "Ivan_12", listOf(react3), "13"
        ),
        ServerApi.Message(
            "Возможность проникновения куда-л., связи, сношения с чем-л.",
            1615928355222, "Ivan_13", listOf(react1), "14"
        ),
        ServerApi.Message(
            "Связь на расстоянии при помощи каких-л. средств, а также средства связи. Железнодорожное сообщение.",
            1616028356222, "Ivan_14", listOf(react2), "15"
        ),
        ServerApi.Message(
            "Сообщение с севером было очень трудно. Почта не действовала. ",
            161602835722, "Ivan_15", listOf(react0), "16"
        ),
        ServerApi.Message(
            "Источник (печатная версия): Словарь русского языка: В 4-х т. / РАН, Ин-т лингвистич. исследований",
            1616028498638, "Ivan_16", listOf(react3), "17"
        ),
        ServerApi.Message(
            "Привет! Как Дела?",
            1615828498638, "ARTUR", listOf(react3), "18"
        ),
        ServerApi.Message(
            "Lorem ipsum test test test",
            1615628498638, "ARTUR", listOf(), "19"
        ),
    )

    override val topicsByStreamUid = mapOf(
        "1" to listOf(
            ServerApi.Topic("Testing1", 1240, "TOPIC_ID_1"),
            ServerApi.Topic("Bruh1", 124, "TOPIC_ID_2")
        ),

        "2" to listOf(
            ServerApi.Topic("Testing2", 12, "TOPIC_ID_3"),
            ServerApi.Topic("Bruh2", 12234, "TOPIC_ID_4")
        ),

        "3" to listOf(
            ServerApi.Topic("Testing3", 38, "TOPIC_ID_5"),
            ServerApi.Topic("Bruh3", 234, "TOPIC_ID_6")
        ),

        "4" to listOf(
            ServerApi.Topic("Testing4", 40, "TOPIC_ID_7"),
            ServerApi.Topic("Bruh4", 14, "TOPIC_ID_8")
        )
    )

    override val subscribedStreamsWithUid = mapOf(
        "#general" to "1",
        "#Development" to "2",
        "#Design" to "3",
        "#PR" to "4"
    )

    override val allStreams = mapOf(
        "#general" to "1",
        "#mems" to "2",
        "#health" to "3",
        "#jobs" to "4",
        "#friends" to "5",
        "#mobile" to "6",
        "#food" to "7",
        "#tinkoff" to "8",
        "#bottle" to "9",
        "#lamp" to "10",
        "#stack" to "11",
        "#fun" to "12",
        "#cooking" to "13",
        "#books" to "14",
        "#cars" to "15",
        "#computers" to "16",
        "#building" to "17",
    )

    override fun getUserNameById(uid: String): String {
        return userList.find { user ->
            user.uid == uid
        }?.userName ?: "none"
    }

    override fun sendMessages(listOfMessages: List<ServerApi.Message>) {
        messages = listOfMessages
    }

    override fun getMessages(): List<ServerApi.Message> {
        return messages
    }

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


}
