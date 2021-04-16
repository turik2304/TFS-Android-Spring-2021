package com.turik2304.coursework.room

import androidx.room.*
import com.turik2304.coursework.recycler_view_base.items.StreamUI
import java.util.concurrent.Flow

@Dao
interface StreamDao {

    @Query("SELECT * FROM streams WHERE isSubscribed = :needSubscribed ORDER BY name ")
    fun getStreams(needSubscribed: Boolean): List<StreamUI>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(streams: List<StreamUI>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(stream: StreamUI)

    @Query("DELETE FROM streams WHERE isSubscribed = :deleteSubscriptions")
    fun deleteStreams(deleteSubscriptions: Boolean)

    @Transaction
    fun deleteAndCreate(deleteSubscriptions: Boolean, newStreams: List<StreamUI>) {
        deleteStreams(deleteSubscriptions)
        insertAll(newStreams)
    }

}
