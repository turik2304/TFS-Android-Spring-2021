package com.turik2304.coursework.room

import androidx.room.TypeConverter
import com.turik2304.coursework.network.models.data.Reaction
import com.turik2304.coursework.network.models.data.StatusEnum
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
    fun fromReactionsList(listOfReactions: List<Reaction>) = Json.encodeToString(listOfReactions)

    @TypeConverter
    fun toReactionsList(reaction: String) = Json.decodeFromString<List<Reaction>>(reaction)

    @TypeConverter
    fun fromIntList(list: List<Int>) = Json.encodeToString(list)

    @TypeConverter
    fun toIntList(int: String) = Json.decodeFromString<List<Int>>(int)

    @TypeConverter
    fun fromStatusEnum(statusEnum: StatusEnum) = Json.encodeToString(statusEnum)

    @TypeConverter
    fun toStatusEnum(statusEnum: String) = Json.decodeFromString<StatusEnum>(statusEnum)


}