package com.turik2304.coursework

import android.app.Application
import com.turik2304.coursework.di.components.DaggerPeopleComponent
import com.turik2304.coursework.di.components.DaggerStreamsComponent
import com.turik2304.coursework.di.components.PeopleComponent
import com.turik2304.coursework.di.components.StreamsComponent

class MyApp : Application() {

//    lateinit var component: AppComponent
    var peopleComponent: PeopleComponent? = null
    var streamsComponent: StreamsComponent? = null

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