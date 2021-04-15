package com.turik2304.coursework.room

import androidx.room.TypeConverter
import com.turik2304.coursework.network.CallHandler
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.DateSeparatorUI
import com.turik2304.coursework.recycler_view_base.items.InMessageUI
import com.turik2304.coursework.recycler_view_base.items.OutMessageUI
import com.turik2304.coursework.recycler_view_base.items.TopicUI
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromTopicsList(list: List<TopicUI>) = Json.encodeToString(list)

    @TypeConverter
    fun toTopicsList(topic: String) = Json.decodeFromString<List<TopicUI>>(topic)

    @TypeConverter
    fun fromReactionsList(listOfReactions: List<CallHandler.Reaction>) = Json.encodeToString(listOfReactions)

    @TypeConverter
    fun toReactionsList(reaction: String) = Json.decodeFromString<List<CallHandler.Reaction>>(reaction)

    @TypeConverter
    fun fromIntList(list: List<Int>) = Json.encodeToString(list)

    @TypeConverter
    fun toIntList(int: String) = Json.decodeFromString<List<Int>>(int)


}