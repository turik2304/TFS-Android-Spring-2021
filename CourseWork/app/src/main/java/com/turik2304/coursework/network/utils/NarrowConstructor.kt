package com.turik2304.coursework.network.utils

object NarrowConstructor {
    fun getNarrow(nameOfTopic: String, nameOfStream: String): String {
        val operand = "\"operand\""
        val operator = "\"operator\""
        val streamKey = "\"stream\""
        val topicKey = "\"topic\""
        val jsonNameOfTopic = "\"$nameOfTopic\""
        val jsonNameOfStream = "\"$nameOfStream\""
        return "[{$operand: $jsonNameOfStream, $operator: $streamKey}," +
                "{$operand: $jsonNameOfTopic, $operator: $topicKey}]"
    }
}