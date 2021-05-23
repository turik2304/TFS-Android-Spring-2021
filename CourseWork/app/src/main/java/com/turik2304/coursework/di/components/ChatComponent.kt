package com.turik2304.coursework.di.components

import com.turik2304.coursework.ChatActivity
import com.turik2304.coursework.di.modules.ChatModule
import com.turik2304.coursework.di.scopes.ChatScope
import com.turik2304.coursework.presentation.view.EmojiView
import dagger.Component

@ChatScope
@Component(dependencies = [AppComponent::class], modules = [ChatModule::class])
interface ChatComponent {
    fun inject(chatActivity: ChatActivity)
    fun inject(emojiView: EmojiView)
}