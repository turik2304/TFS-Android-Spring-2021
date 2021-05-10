package com.turik2304.coursework.data.room

import android.content.Context
import androidx.room.Room

object DatabaseClient {

    private var instance: Database? = null

    fun getInstance(context: Context): Database? {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.applicationContext,
                Database::class.java,
                "MyDatabase"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
        return instance
    }
}