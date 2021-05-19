package com.turik2304.coursework

import android.app.Application
import com.turik2304.coursework.di.components.*

class MyApp : Application() {

    //    lateinit var component: AppComponent
    var peopleComponent: PeopleComponent? = null
    var streamsComponent: StreamsComponent? = null
    var chatComponent: ChatComponent? = null

    companion object {
        lateinit var app: Application
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    fun addPeopleComponent() {
        if (peopleComponent == null) {
            peopleComponent = DaggerPeopleComponent.create()
        }
    }

    fun addStreamsComponent() {
        if (streamsComponent == null) {
            streamsComponent = DaggerStreamsComponent.create()
        }
    }

    fun addChatComponent() {
        if (chatComponent == null) {
            chatComponent = DaggerChatComponent.create()
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