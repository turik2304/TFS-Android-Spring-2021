package com.turik2304.coursework.di.components

import com.turik2304.coursework.di.modules.PeopleModule
import com.turik2304.coursework.di.scopes.PeoplesScope
import com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments.OwnProfileFragment
import com.turik2304.coursework.presentation.fragments.bottom_navigation_fragments.PeopleFragment
import dagger.Component

@PeoplesScope
@Component(modules = [PeopleModule::class])
interface PeopleComponent {
    fun inject(peopleFragment: PeopleFragment)
    fun inject(ownProfileFragment: OwnProfileFragment)
}