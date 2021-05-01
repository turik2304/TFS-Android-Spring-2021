package com.turik2304.coursework.data.room

import androidx.room.*
import com.turik2304.coursework.presentation.recycler_view.items.UserUI

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY userName")
    fun getAll(): List<UserUI>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<UserUI>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserUI)

    @Query("DELETE FROM users")
    fun deleteAll()

    @Transaction
    fun deleteAndCreate(users: List<UserUI>) {
        deleteAll()
        insertAll(users)
    }

}