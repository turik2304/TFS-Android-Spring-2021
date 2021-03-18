package com.turik2304.coursework

import com.turik2304.coursework.recyclerViewBase.ViewTyped
import java.util.*

class FakeServerApi {

    data class User(val uid: String, val userName: String)
    data class Message(
        val message: String,
        val dateInMillis: Long,
        val uid: String,
        val reactions: List<Reaction>
    )

    data class Reaction(val emojiCode: Int, val counter: Int, val usersWhoClicked: List<String>)

    private val userList = listOf(
        User("ARTUR", "Artur Sibagatullin"),
        User("Ivan_0", "Ivan Ivanov_0"),
        User("Ivan_1", "Ivan Ivanov_1"),
        User("Ivan_2", "Ivan Ivanov_2"),
        User("Ivan_3", "Ivan Ivanov_3"),
        User("Ivan_4", "Ivan Ivanov_4"),
        User("Ivan_5", "Ivan Ivanov_5"),
        User("Ivan_6", "Ivan Ivanov_6"),
        User("Ivan_7", "Ivan Ivanov_7"),
        User("Ivan_8", "Ivan Ivanov_8"),
        User("Ivan_9", "Ivan Ivanov_9"),
        User("Ivan_10", "Ivan Ivanov_10"),
        User("Ivan_11", "Ivan Ivanov_11"),
        User("Ivan_12", "Ivan Ivanov_12"),
        User("Ivan_13", "Ivan Ivanov_13"),
        User("Ivan_14", "Ivan Ivanov_14"),
        User("Ivan_15", "Ivan Ivanov_15"),
        User("Ivan_16", "Ivan Ivanov_16"),
    )

    private val react0 = Reaction(0x1F600, 1, listOf("ARTUR"))
    private val react1 = Reaction(0x1F601, 2, listOf("ARTUR", "DENIS"))
    private val react2 = Reaction(0x1F602, 3, listOf("IVAN", "ROMA", "ALEX"))
    private val react3 = Reaction(0x1F603, 4, listOf("ANDREI", "ARTUR", "ROMA", "ALEX"))

    var messages = mutableListOf(
        Message(
            "– Буддлея! – с досадой проговорил он, прочитав текст сообщения.",
            1615628355222, "Ivan_0", listOf(react0)
        ),
        Message(
            "Наконец, для большей эффективности в тексте сообщения следует максимально использовать все возможные элементы",
            1615628356222, "Ivan_1", listOf(react0)
        ),
        Message(
            "Он бегло просмотрел тексты сообщений, отобранных и систематизированных для него кибернетической системой логической обработки данных",
            1615728355222, "Ivan_2", listOf(react1)
        ),
        Message(
            "ТЕКСТ, -а, м. 1. Слова, предложения в определенной связи и последовательности, образующие какое-л. высказывание, сочинение,",
            1615728356222, "Ivan_3", listOf(react2)
        ),
        Message(
            "Текст воинской присяги. Текст пьесы. Записать текст сказки. (Малый академический словарь, МАС)",
            1615728357222, "Ivan_4", listOf(react3)
        ),
        Message(
            "Привет! Меня зовут Лампобот, я компьютерная программа, которая помогает делать Карту слов",
            1615728358222, "Ivan_5", listOf(react1, react2)
        ),
        Message(
            "Действие по знач. глаг. сообщить—сообщать и сообщиться—сообщаться. ",
            1615728359222, "Ivan_6", listOf(react3)
        ),
        Message(
            "Осенью 1913 года весь мир облетело сенсационное сообщение об открытии русскими моряками неизвестных земель",
            1615828360222, "Ivan_7", listOf(react0)
        ),
        Message(
            "По нехоженой земле. О расстреле мирной манифестации рабочих у Зимнего дворца первым принес сообщение Антон Топилкин. Марков, Строговы",
            1615828361222, "Ivan_8", listOf(react2)
        ),
        Message(
            "Данные, сведения, передаваемые, сообщаемые, излагаемые кем-л. Сообщение бюро погоды.",
            1615828355222, "Ivan_9", listOf(react1)
        ),
        Message(
            "Офицер доложил последнее сообщение рации; за исключением тридцать седьмой, размещение корпуса закончилось. ",
            1615928356222, "Ivan_10", listOf(react1)
        ),
        Message(
            "Леонов, Взятие Великошумска. ",
            1615928357222, "Ivan_11", listOf(react2, react3, react1)
        ),
        Message(
            "Небольшой доклад на какую-л. тему, информация. Котельников поехал в институт, к профессору Карелину, делать какое-то сообщение на кафедре. ",
            1615928358222, "Ivan_12", listOf(react3)
        ),
        Message(
            "Возможность проникновения куда-л., связи, сношения с чем-л.",
            1615928355222, "Ivan_13", listOf(react1)
        ),
        Message(
            "Связь на расстоянии при помощи каких-л. средств, а также средства связи. Железнодорожное сообщение.",
            1616028356222, "Ivan_14", listOf(react2)
        ),
        Message(
            "Сообщение с севером было очень трудно. Почта не действовала. ",
            161602835722, "Ivan_15", listOf(react0)
        ),
        Message(
            "Источник (печатная версия): Словарь русского языка: В 4-х т. / РАН, Ин-т лингвистич. исследований",
            1616028498638, "Ivan_16", listOf(react3)
        ),
        Message(
            "Привет! Как Дела?",
            1615828498638, "ARTUR", listOf(react3)
        ),
        Message(
            "Lorem ipsum test test test",
            1615628498638, "ARTUR", listOf()
        ),
    )

    fun getUserNameById(uid: String): String {
        return userList.find { user ->
            user.uid == uid
        }?.userName ?: "none"
    }
}
