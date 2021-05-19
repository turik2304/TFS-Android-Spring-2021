package com.turik2304.coursework.data.room

import androidx.room.*
import com.turik2304.coursework.data.network.models.data.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users ORDER BY userName")
    fun getAll(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("DELETE FROM users")
    fun deleteAll()

    @Transaction
    fun deleteAndCreate(users: List<User>) {
        deleteAll()
        insertAll(users)
    }
}