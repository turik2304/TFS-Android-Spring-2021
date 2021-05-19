package com.turik2304.coursework

import android.app.Application
import com.turik2304.coursework.di.components.*
import com.turik2304.coursework.di.modules.app_modules.AppModule

class MyApp : Application() {

    lateinit var appComponent: AppComponent
    var peopleComponent: PeopleComponent? = null
    var streamsComponent: StreamsComponent? = null
    var chatComponent: ChatComponent? = null

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
        appComponent.inject(this)
    }

    fun addPeopleComponent() {
        if (peopleComponent == null) {
            peopleComponent = DaggerPeopleComponent
                .builder()
                .appComponent(appComponent)
                .build()
        }
    }

    fun addStreamsComponent() {
        if (streamsComponent == null) {
            streamsComponent = DaggerStreamsComponent
                .builder()
                .appComponent(appComponent)
                .build()
        }
    }

    fun addChatComponent() {
        if (chatComponent == null) {
            chatComponent = DaggerChatComponent
                .builder()
                .appComponent(appComponent)
                .build()
        }
    }

    fun clearChatComponent() {
        chatComponent = null
    }

    fun clearStreamsComponent() {
        streamsComponent = null
    }


    fun clearPeopleComponent() {
        peopleComponent = null
    }

    fun clearAllComponents() {
        peopleComponent = null
        streamsComponent = null
    }


}