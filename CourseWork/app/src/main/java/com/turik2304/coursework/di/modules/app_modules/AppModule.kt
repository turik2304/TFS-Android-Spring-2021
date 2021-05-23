package com.turik2304.coursework.di.modules.app_modules

import android.app.Application
import android.content.Context
import com.turik2304.coursework.di.scopes.AppScope
import dagger.Module
import dagger.Provides

@Module
class AppModule(val application: Application) {

    @Provides
    @AppScope
    fun provideContext(): Context = application
}