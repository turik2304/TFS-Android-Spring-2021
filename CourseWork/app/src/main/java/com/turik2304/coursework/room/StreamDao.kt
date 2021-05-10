package com.turik2304.coursework.room

import androidx.room.*
import com.turik2304.coursework.recycler_view_base.items.StreamUI

@Dao
interface StreamDao {

    @Query("SELECT * FROM streams WHERE NOT isSubscribed = :needAllStreams ORDER BY name ")
    fun getStreams(needAllStreams: Boolean): List<StreamUI>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(streams: List<StreamUI>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stream: StreamUI)

    @Query("DELETE FROM streams WHERE NOT isSubscribed = :deleteAllStreams")
    fun deleteStreams(deleteAllStreams: Boolean)

    @Transaction
    fun deleteAndCreate(deleteAllStreams: Boolean, newStreams: List<StreamUI>) {
        deleteStreams(deleteAllStreams)
        insertAll(newStreams)
    }

}
