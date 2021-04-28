package com.turik2304.coursework.network.utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object NarrowConstructor {

    fun getNarrow(nameOfTopic: String, nameOfStream: String): String {
        val operand = "\"operand\""
        val operator = "\"operator\""
        val streamKey = "\"stream\""
        val topicKey = "\"topic\""
        val jsonNameOfTopic = Json.encodeToString(nameOfTopic)
        val jsonNameOfStream = Json.encodeToString(nameOfStream)
        return "[{$operand: $jsonNameOfStream, $operator: $streamKey}," +
                "{$operand: $jsonNameOfTopic, $operator: $topicKey}]"
    }

    fun getNarrowArray(nameOfTopic: String, nameOfStream: String): String {
        val streamKey = "\"stream\""
        val topicKey = "\"topic\""
        val jsonNameOfTopic = Json.encodeToString(nameOfTopic)
        val jsonNameOfStream = Json.encodeToString(nameOfStream)
        return "[[$streamKey, $jsonNameOfStream], [$topicKey, $jsonNameOfTopic]]"
    }
}

