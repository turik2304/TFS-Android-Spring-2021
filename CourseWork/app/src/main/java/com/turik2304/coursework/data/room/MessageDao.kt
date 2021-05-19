package com.turik2304.coursework.data.room

import androidx.room.*
import com.turik2304.coursework.data.network.models.data.Message

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic")
    fun getAll(nameOfStream: String, nameOfTopic: String): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(messages: List<Message>)

    @Update
    fun update(messages: List<Message>)

    @Query("SELECT COUNT(*) FROM messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic")
    fun getCount(nameOfStream: String, nameOfTopic: String): Int

    @Query("DELETE FROM messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic")
    fun deleteAll(nameOfStream: String, nameOfTopic: String)

    @Transaction
    fun deleteAndCreate(
        nameOfStream: String,
        nameOfTopic: String,
        messages: List<Message>
    ) {
        deleteAll(nameOfStream, nameOfTopic)
        insertAll(messages)
    }

    @Query("DELETE FROM messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic AND id NOT IN (SELECT id from messages WHERE nameOfStream = :nameOfStream AND nameOfTopic = :nameOfTopic ORDER BY id DESC LIMIT 50)")
    fun checkCapacity(nameOfStream: String, nameOfTopic: String)

    @Transaction
    fun insertAllAndCheckCapacity(
        nameOfStream: String,
        nameOfTopic: String,
        messages: List<Message>
    ) {
        insertAll(messages)
        checkCapacity(nameOfStream, nameOfTopic)
    }
}