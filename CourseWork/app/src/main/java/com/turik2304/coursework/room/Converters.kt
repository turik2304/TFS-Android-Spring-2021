package com.turik2304.coursework.room

import androidx.room.TypeConverter
import com.turik2304.coursework.recycler_view_base.items.TopicUI
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromTopicsList(list: List<TopicUI>) = Json.encodeToString(list)

    @TypeConverter
    fun toTopicsList(topic: String) = Json.decodeFromString<List<TopicUI>>(topic)
}