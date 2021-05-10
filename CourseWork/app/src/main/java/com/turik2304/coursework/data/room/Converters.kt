package com.turik2304.coursework.data.room

import androidx.room.TypeConverter
import com.turik2304.coursework.data.network.models.data.Reaction
import com.turik2304.coursework.data.network.models.data.StatusEnum
import com.turik2304.coursework.data.network.models.data.Topic
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromTopicsList(list: List<Topic>) = Json.encodeToString(list)

    @TypeConverter
    fun toTopicsList(topic: String) = Json.decodeFromString<List<Topic>>(topic)

    @TypeConverter
    fun fromReactionsList(listOfReactions: List<Reaction>) = Json.encodeToString(listOfReactions)

    @TypeConverter
    fun toReactionsList(reaction: String) = Json.decodeFromString<List<Reaction>>(reaction)

    @TypeConverter
    fun fromStatusEnum(statusEnum: StatusEnum) = Json.encodeToString(statusEnum)

    @TypeConverter
    fun toStatusEnum(statusEnum: String) = Json.decodeFromString<StatusEnum>(statusEnum)
}