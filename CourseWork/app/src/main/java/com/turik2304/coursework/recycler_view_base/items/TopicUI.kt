package com.turik2304.coursework.recycler_view_base.items

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.ViewTyped
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "topics",
        foreignKeys = [ForeignKey(entity = StreamUI::class,
                parentColumns = arrayOf("uid"),
                childColumns = arrayOf("streamUid"),
                onDelete = ForeignKey.CASCADE)])
@Serializable
data class TopicUI(
        @SerialName("name")
        val name: String,
        val numberOfMessages: String = "1240 mes",
        @PrimaryKey
        @SerialName("max_id")
        override val uid: Int,
        var streamUid: Int = -1,
        override var viewType: Int = R.layout.item_topic,
) : ViewTyped
