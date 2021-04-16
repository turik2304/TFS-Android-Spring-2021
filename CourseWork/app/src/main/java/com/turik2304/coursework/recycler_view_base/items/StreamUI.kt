package com.turik2304.coursework.recycler_view_base.items

import androidx.room.*
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.ViewTyped
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "streams")
@Serializable
data class StreamUI(
        @SerialName("name")
        val name: String,
        @PrimaryKey
        @SerialName("stream_id")
        override val uid: Int,
        var topics: List<TopicUI> = emptyList(),
        var isExpanded: Boolean = false,
        var isSubscribed: Boolean = false,
        override val viewType: Int = R.layout.item_stream,
) : ViewTyped