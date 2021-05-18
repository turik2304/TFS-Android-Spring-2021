package com.turik2304.coursework

import android.app.Application
import com.turik2304.coursework.di.components.DaggerPeopleComponent
import com.turik2304.coursework.di.components.PeopleComponent

class MyApp : Application() {

//    lateinit var component: AppComponent
    var peopleComponent: PeopleComponent? = null

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

    fun clearPeopleComponent() {
        peopleComponent = null
    }

    fun clearAllComponents() {
        peopleComponent = null
    }


}