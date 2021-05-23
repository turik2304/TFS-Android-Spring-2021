package com.turik2304.coursework.data.network.utils

import org.json.JSONArray

interface NarrowConstructor {
    fun getNarrow(nameOfTopic: String, nameOfStream: String): JSONArray
    fun getNarrowArray(nameOfTopic: String, nameOfStream: String): JSONArray
}