package com.turik2304.coursework.network.models.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.turik2304.coursework.recycler_view_base.items.InMessageUI
import kotlinx.serialization.Serializable

@Entity(
    tableName = "reactions",
    foreignKeys = [ForeignKey(
        entity = InMessageUI::class,
        parentColumns = arrayOf("uid"),
        childColumns = arrayOf("uidOfMessage"),
        onDelete = ForeignKey.CASCADE
    )]
)
@Serializable
data class Reaction(
    @PrimaryKey
    val emojiCode: Int,
    var counter: Int,
    val usersWhoClicked: MutableList<Int>,
    val uidOfMessage: Int
)