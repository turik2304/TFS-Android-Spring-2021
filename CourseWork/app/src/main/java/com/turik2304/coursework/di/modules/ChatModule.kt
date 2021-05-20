package com.turik2304.coursework.di.modules

import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.data.repository.Repository
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
    ): Store<ChatActions, ChatUiState> = Store(
        reducer = reducer,
        middlewares = middlewares,
        initialState = initialState
    )

    @Provides
    @ChatScope
    fun provideReducer(): ChatReducer = ChatReducer()

    @Provides
    @ChatScope
    fun provideChatMiddlewares(repository: Repository): List<Middleware<ChatActions, ChatUiState>> =
        listOf(
            LoadMessagesMiddleware(repository),
            RegisterEventsMiddleware(repository),
            EventsLongpollingMiddleware(repository),
            SendMessageMiddleware(repository),
            AddReactionMiddleware(repository),
            RemoveReactionMiddleware(repository),
            GetBottomSheetReactionsMiddleware(repository)
        )

    @Provides
    @ChatScope
    fun provideInitialState(): ChatUiState = ChatUiState()

    @Provides
    @ChatScope
    fun provideActions(): PublishRelay<ChatActions> = PublishRelay.create()

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()
}