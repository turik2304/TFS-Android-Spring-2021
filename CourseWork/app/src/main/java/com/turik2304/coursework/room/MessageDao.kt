package com.turik2304.coursework.room

import androidx.room.*
import com.turik2304.coursework.MessagesExt.toInMessages
import com.turik2304.coursework.MessagesExt.toViewTypedMessages
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.InMessageUI

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic")
    fun getRaw(nameOfStream: String, nameOfTopic: String): List<InMessageUI>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConverted(inMessages: List<InMessageUI>)

    @Query("DELETE FROM messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic")
    fun deleteAll(nameOfStream: String, nameOfTopic: String)

    @Transaction
    fun deleteAndCreate(nameOfStream: String, nameOfTopic: String, viewTypedMessages: List<ViewTyped>) {
        deleteAll(nameOfStream, nameOfTopic)
        insertAll(viewTypedMessages)
    }

    fun insertAll(viewTypedMessages: List<ViewTyped>) {
        insertConverted(viewTypedMessages.toInMessages())
    }

    fun getAll(nameOfStream: String, nameOfTopic: String): List<ViewTyped> {
        return getRaw(nameOfStream, nameOfTopic).toViewTypedMessages()
    }

}
