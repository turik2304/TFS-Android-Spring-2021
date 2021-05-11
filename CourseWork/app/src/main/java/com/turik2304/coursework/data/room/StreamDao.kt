package com.turik2304.coursework.data.room

import androidx.room.*
import com.turik2304.coursework.data.network.models.data.Stream

@Dao
interface StreamDao {

    @Query("SELECT * FROM streams WHERE NOT isSubscribed = :needAllStreams ORDER BY nameOfStream ")
    fun getStreams(needAllStreams: Boolean): List<Stream>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(streams: List<Stream>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stream: Stream)

    @Query("DELETE FROM streams WHERE NOT isSubscribed = :deleteAllStreams")
    fun deleteStreams(deleteAllStreams: Boolean)

    @Transaction
    fun deleteAndCreate(deleteAllStreams: Boolean, newStreams: List<Stream>) {
        deleteStreams(deleteAllStreams)
        insertAll(newStreams)
    }

}
