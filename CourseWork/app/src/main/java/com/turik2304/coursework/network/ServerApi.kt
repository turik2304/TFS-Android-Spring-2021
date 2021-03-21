package com.turik2304.coursework.network

interface ServerApi {
    data class User(val uid: String, val userName: String)
    data class Reaction(val emojiCode: Int, val counter: Int, val usersWhoClicked: List<String>)
    data class Message(
        val message: String,
        val dateInMillis: Long,
        val userId: String,
        val reactions: List<Reaction>,
        val uid: String
    )
    val userList: List<User>
    fun getUserNameById(uid: String): String
    fun sendMessages(listOfMessages: List<Message>)
    fun getMessages(): List<Message>
}