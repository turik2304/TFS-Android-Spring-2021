package com.turik2304.coursework.recycler_view_base.items

import androidx.room.PrimaryKey
import com.turik2304.coursework.R
import com.turik2304.coursework.recycler_view_base.ViewTyped
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopicUI(
        @SerialName("name")
        val name: String,
        val numberOfMessages: String = "1240 mes",
        @PrimaryKey
        @SerialName("max_id")
        override val uid: Int,
        override var viewType: Int = R.layout.item_topic,
) : ViewTyped
