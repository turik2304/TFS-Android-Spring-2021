package com.turik2304.coursework.room

import androidx.room.*
import com.turik2304.coursework.MessagesExt.toInMessages
import com.turik2304.coursework.MessagesExt.toViewTypedMessages
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.items.InMessageUI


@Dao
interface MessageDao {

    @Transaction
    @Query("SELECT * FROM messages")
    fun getRaw(): List<InMessageUI>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertConverted(inMessages: List<InMessageUI>)

    fun insertAll(viewTypedMessages: List<ViewTyped>) {
        insertConverted(viewTypedMessages.toInMessages())
    }

    fun getAll(): List<ViewTyped> {
        return getRaw().toViewTypedMessages()
    }

}
