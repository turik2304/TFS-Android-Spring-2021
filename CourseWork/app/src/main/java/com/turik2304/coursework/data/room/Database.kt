package com.turik2304.coursework.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.turik2304.coursework.data.network.models.data.Stream
import com.turik2304.coursework.data.network.models.data.User
import com.turik2304.coursework.presentation.recycler_view.items.InMessageUI

@Database(
    entities = [
        User::class,
        Stream::class,
        InMessageUI::class,
    ], version = 1
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun streamDao(): StreamDao
    abstract fun messageDao(): MessageDao

}