package com.turik2304.coursework.room

import androidx.room.*
import com.turik2304.coursework.extensions.toInMessages
import com.turik2304.coursework.extensions.toViewTypedMessages
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.InMessageUI

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic")
    fun getRaw(nameOfStream: String, nameOfTopic: String): List<InMessageUI>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConverted(inMessages: List<InMessageUI>)

    @Update
    fun updateConverted(inMessages: List<InMessageUI>)

    @Query("SELECT COUNT(*) FROM messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic")
    fun getCount(nameOfStream: String, nameOfTopic: String): Int

    @Query("DELETE FROM messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic")
    fun deleteAll(nameOfStream: String, nameOfTopic: String)

    @Transaction
    fun deleteAndCreate(
        nameOfStream: String,
        nameOfTopic: String,
        viewTypedMessages: List<ViewTyped>
    ) {
        deleteAll(nameOfStream, nameOfTopic)
        insertAll(viewTypedMessages)
    }

    @Query("DELETE FROM messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic AND uid NOT IN (SELECT uid from messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic ORDER BY uid DESC LIMIT 50)")
    fun checkCapacity(nameOfStream: String, nameOfTopic: String)

    fun update(viewTypedMessages: List<ViewTyped>) {
        updateConverted(viewTypedMessages.toInMessages())
    }

    fun insertAll(viewTypedMessages: List<ViewTyped>) {
        insertConverted(viewTypedMessages.toInMessages())
    }

    @Transaction
    fun insertAllAndCheckCapacity(
        nameOfStream: String,
        nameOfTopic: String,
        viewTypedMessages: List<ViewTyped>
    ) {
        insertConverted(viewTypedMessages.toInMessages())
        checkCapacity(nameOfStream, nameOfTopic)
    }

    fun getAll(nameOfStream: String, nameOfTopic: String): List<ViewTyped> {
        return getRaw(nameOfStream, nameOfTopic).toViewTypedMessages()
    }

}
