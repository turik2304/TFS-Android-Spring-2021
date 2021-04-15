package com.turik2304.coursework.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.turik2304.coursework.network.CallHandler
import com.turik2304.coursework.recycler_view_base.items.*


@Database(entities = [
    UserUI::class,
    StreamUI::class,
    TopicUI::class,
    InMessageUI::class,
    CallHandler.Reaction::class
], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun streamDao(): StreamDao
    abstract fun messageDao(): MessageDao

}