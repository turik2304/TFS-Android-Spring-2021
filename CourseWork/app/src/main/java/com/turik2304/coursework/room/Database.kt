package com.turik2304.coursework.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.turik2304.coursework.recycler_view_base.items.UserUI


@Database(entities = [
    UserUI::class
], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao

}