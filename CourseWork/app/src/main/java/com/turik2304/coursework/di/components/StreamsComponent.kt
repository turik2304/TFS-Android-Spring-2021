package com.turik2304.coursework.di.components

import com.turik2304.coursework.di.modules.StreamsModule
import com.turik2304.coursework.di.scopes.StreamsScope
import com.turik2304.coursework.presentation.fragments.view_pager_fragments.AllStreamsFragment
import com.turik2304.coursework.presentation.fragments.view_pager_fragments.SubscribedStreamsFragment
import dagger.Component

@StreamsScope
@Component(modules = [StreamsModule::class])
interface StreamsComponent {
    fun inject(subscribedStreamsFragment: SubscribedStreamsFragment)
    fun inject(allStreamsFragment: AllStreamsFragment)
}