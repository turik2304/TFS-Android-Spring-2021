package com.turik2304.coursework.di.modules

import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.di.scopes.ChatScope
import com.turik2304.coursework.domain.Middleware
import com.turik2304.coursework.domain.chat_middlewares.*
import com.turik2304.coursework.presentation.ChatActions
import com.turik2304.coursework.presentation.ChatReducer
import com.turik2304.coursework.presentation.ChatUiState
import com.turik2304.coursework.presentation.base.Store
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.disposables.CompositeDisposable

@Module
class ChatModule {

    @Provides
    @ChatScope
    fun provideChatStore(
        reducer: ChatReducer,
        middlewares: List<@JvmSuppressWildcards Middleware<ChatActions, ChatUiState>>,
        initialState: ChatUiState
    ): Store<ChatActions, ChatUiState> {
        return Store(
            reducer = reducer,
            middlewares = middlewares,
            initialState = initialState
        )
    }

    @Provides
    @ChatScope
    fun provideReducer(): ChatReducer {
        return ChatReducer()
    }

    @Provides
    @ChatScope
    fun provideChatMiddlewares(): List<Middleware<ChatActions, ChatUiState>> {
        return listOf(
            LoadMessagesMiddleware(),
            RegisterEventsMiddleware(),
            EventsLongpollingMiddleware(),
            SendMessageMiddleware(),
            AddReactionMiddleware(),
            RemoveReactionMiddleware()
        )
    }

    @Provides
    @ChatScope
    fun provideInitialState(): ChatUiState {
        return ChatUiState()
    }

    @Provides
    @ChatScope
    fun provideActions(): PublishRelay<ChatActions> {
        return PublishRelay.create()
    }

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }
}