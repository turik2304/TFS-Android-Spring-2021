package com.turik2304.coursework.room

import androidx.room.*
import com.turik2304.coursework.recycler_view_base.items.StreamUI

@Dao
interface StreamDao {

    @Query("SELECT * FROM streams ORDER BY name")
    fun getAll(): List<StreamUI>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(streams: List<StreamUI>)

    @Query("DELETE FROM streams")
    fun deleteAll()

    @Transaction
    fun deleteAndCreate(streams: List<StreamUI>) {
        deleteAll()
        insertAll(streams)
    }

}
