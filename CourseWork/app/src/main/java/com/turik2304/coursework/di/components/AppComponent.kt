package com.turik2304.coursework.di.components

import com.turik2304.coursework.MyApp
import com.turik2304.coursework.di.modules.AppModule
import com.turik2304.coursework.di.scopes.AppScope
import dagger.Component

@Component(modules = [AppModule::class])
@AppScope
interface AppComponent {
    fun inject(app: MyApp)
}