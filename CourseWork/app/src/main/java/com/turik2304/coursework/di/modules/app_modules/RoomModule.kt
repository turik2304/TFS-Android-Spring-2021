package com.turik2304.coursework.di.modules.app_modules

import android.content.Context
import androidx.room.Room
import com.turik2304.coursework.data.room.Database
import com.turik2304.coursework.di.scopes.AppScope
import dagger.Module
import dagger.Provides

@Module
class RoomModule {

    @Provides
    @AppScope
    fun provideDatabase(context: Context): Database = Room.databaseBuilder(
        context.applicationContext,
        Database::class.java,
        "MyDatabase"
    )
        .fallbackToDestructiveMigration()
        .build()
}