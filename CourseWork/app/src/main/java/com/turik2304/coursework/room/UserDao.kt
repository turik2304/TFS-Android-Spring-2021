package com.turik2304.coursework.room

import androidx.room.*
import com.turik2304.coursework.recycler_view_base.items.UserUI

@Dao
interface UserDao {
    @Query("SELECT * FROM user ORDER BY userName")
    fun getAll(): List<UserUI>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user:UserUI)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<UserUI>)

    @Delete
    fun delete(user: UserUI)

    @Query("DELETE FROM user")
    fun deleteAll()

    @Transaction
    fun deleteAndCreate(users: List<UserUI>) {
        deleteAll()
        insertAll(users)
    }

}