package com.turik2304.coursework.di.components

import android.app.Application
import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.room.Database
import com.turik2304.coursework.di.modules.app_modules.*
import com.turik2304.coursework.di.scopes.AppScope
import dagger.Component

@AppScope
@Component(modules = [AppModule::class, RepositoryModule::class, RetrofitModule::class, OkHttpModule::class, RoomModule::class])
interface AppComponent {
    fun inject(app: Application)
    fun db(): Database
    fun repo(): Repository
}