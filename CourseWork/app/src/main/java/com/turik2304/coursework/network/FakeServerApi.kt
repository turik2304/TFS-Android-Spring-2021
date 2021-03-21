package com.turik2304.coursework.network

class FakeServerApi: ServerApi {

    override val userList = listOf(
        ServerApi.User("ARTUR", "Artur Sibagatullin"),
        ServerApi.User("Ivan_0", "Ivan Ivanov_0"),
        ServerApi.User("Ivan_1", "Ivan Ivanov_1"),
        ServerApi.User("Ivan_2", "Ivan Ivanov_2"),
        ServerApi.User("Ivan_3", "Ivan Ivanov_3"),
        ServerApi.User("Ivan_4", "Ivan Ivanov_4"),
        ServerApi.User("Ivan_5", "Ivan Ivanov_5"),
        ServerApi.User("Ivan_6", "Ivan Ivanov_6"),
        ServerApi.User("Ivan_7", "Ivan Ivanov_7"),
        ServerApi.User("Ivan_8", "Ivan Ivanov_8"),
        ServerApi.User("Ivan_9", "Ivan Ivanov_9"),
        ServerApi.User("Ivan_10", "Ivan Ivanov_10"),
        ServerApi.User("Ivan_11", "Ivan Ivanov_11"),
        ServerApi.User("Ivan_12", "Ivan Ivanov_12"),
        ServerApi.User("Ivan_13", "Ivan Ivanov_13"),
        ServerApi.User("Ivan_14", "Ivan Ivanov_14"),
        ServerApi.User("Ivan_15", "Ivan Ivanov_15"),
        ServerApi.User("Ivan_16", "Ivan Ivanov_16"),
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
}
