package com.turik2304.coursework.di.modules

import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.di.scopes.PeoplesScope
import com.turik2304.coursework.di.scopes.StreamsScope
import com.turik2304.coursework.domain.AllStreamsMiddleware
import com.turik2304.coursework.domain.SubscribedStreamsMiddleware
import com.turik2304.coursework.presentation.StreamsActions
import com.turik2304.coursework.presentation.StreamsReducer
import com.turik2304.coursework.presentation.StreamsUiState
import com.turik2304.coursework.presentation.base.Store
import dagger.Module
import dagger.Provides
import io.reactivex.rxjava3.disposables.CompositeDisposable
import javax.inject.Named

@Module
class StreamsModule {

    companion object {
        const val SUBSCRIBED_STREAMS_STORE = "SUBSCRIBED"
        const val ALL_STREAMS_STORE = "ALL_STREAMS"
    }

    @Provides
    @StreamsScope
    @Named(SUBSCRIBED_STREAMS_STORE)
    fun provideSubscribedStore(
        reducer: StreamsReducer,
        middlewares: List<SubscribedStreamsMiddleware>,
        initialState: StreamsUiState
    ): Store<StreamsActions, StreamsUiState> {
        return Store(
            reducer = reducer,
            middlewares = middlewares,
            initialState = initialState
        )
    }

    @Provides
    @StreamsScope
    @Named(ALL_STREAMS_STORE)
    fun provideAllStreamsStore(
        reducer: StreamsReducer,
        middlewares: List<AllStreamsMiddleware>,
        initialState: StreamsUiState
    ): Store<StreamsActions, StreamsUiState> {
        return Store(
            reducer = reducer,
            middlewares = middlewares,
            initialState = initialState
        )
    }

    @Provides
    @StreamsScope
    fun provideReducer(): StreamsReducer {
        return StreamsReducer()
    }

    @Provides
    @StreamsScope
    fun provideSubscribedStreamsMiddleware(): List<SubscribedStreamsMiddleware> {
        return listOf(SubscribedStreamsMiddleware())
    }

    @Provides
    @StreamsScope
    fun provideAllStreamsMiddlewares(): List<AllStreamsMiddleware> {
        return listOf(AllStreamsMiddleware())
    }

    @Provides
    @StreamsScope
    fun provideInitialState(): StreamsUiState {
        return StreamsUiState()
    }

    @Provides
    fun provideActions(): PublishRelay<StreamsActions> {
        return PublishRelay.create()
    }

    @Provides
    @StreamsScope
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }
}