package com.turik2304.coursework.data.network.utils

import org.json.JSONArray
import org.json.JSONObject

class NarrowConstructorImpl : NarrowConstructor {

    companion object {
        private const val OPERAND_KEY = "operand"
        private const val OPERATOR_KEY = "operator"
        private const val OPERATOR_VALUE_STREAM = "stream"
        private const val OPERATOR_VALUE_TOPIC = "topic"
    }

    override fun getNarrow(nameOfTopic: String, nameOfStream: String): JSONArray {
        val jsonObjStream = JSONObject()
        jsonObjStream.put(OPERAND_KEY, nameOfStream)
        jsonObjStream.put(OPERATOR_KEY, OPERATOR_VALUE_STREAM)

        val jsonObjTopic = JSONObject()
        jsonObjTopic.put(OPERAND_KEY, nameOfTopic)
        jsonObjTopic.put(OPERATOR_KEY, OPERATOR_VALUE_TOPIC)

        val jsonArray = JSONArray()
        jsonArray.put(jsonObjStream)
        jsonArray.put(jsonObjTopic)
        return jsonArray
    }

    override fun getNarrowArray(nameOfTopic: String, nameOfStream: String): JSONArray {
        val jsonArrayStream = JSONArray()
        jsonArrayStream.put(OPERATOR_VALUE_STREAM)
        jsonArrayStream.put(nameOfStream)

        val jsonArrayTopic = JSONArray()
        jsonArrayTopic.put(OPERATOR_VALUE_TOPIC)
        jsonArrayTopic.put(nameOfTopic)

        val commonJsonArray = JSONArray()
        commonJsonArray.put(jsonArrayStream)
        commonJsonArray.put(jsonArrayTopic)
        return commonJsonArray
    }
}

