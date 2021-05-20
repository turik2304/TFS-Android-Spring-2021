package com.turik2304.coursework.di.modules

import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.di.scopes.StreamsScope
import com.turik2304.coursework.domain.AllStreamsMiddleware
import com.turik2304.coursework.domain.SubscribedStreamsMiddleware
import com.turik2304.coursework.presentation.StreamsActions
import com.turik2304.coursework.presentation.StreamsReducer
import com.turik2304.coursework.presentation.StreamsUiState
import com.turik2304.coursework.presentation.base.Store
import com.turik2304.coursework.presentation.recycler_view.DiffCallback
import com.turik2304.coursework.presentation.recycler_view.base.HolderFactory
import com.turik2304.coursework.presentation.recycler_view.base.ViewTyped
import com.turik2304.coursework.presentation.recycler_view.holder_factories.MainHolderFactory
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
    ): Store<StreamsActions, StreamsUiState> = Store(
        reducer = reducer,
        middlewares = middlewares,
        initialState = initialState
    )

    @Provides
    @StreamsScope
    @Named(ALL_STREAMS_STORE)
    fun provideAllStreamsStore(
        reducer: StreamsReducer,
        middlewares: List<AllStreamsMiddleware>,
        initialState: StreamsUiState
    ): Store<StreamsActions, StreamsUiState> = Store(
        reducer = reducer,
        middlewares = middlewares,
        initialState = initialState
    )

    @Provides
    @StreamsScope
    fun provideReducer(): StreamsReducer = StreamsReducer()

    @Provides
    @StreamsScope
    fun provideSubscribedStreamsMiddleware(repository: Repository): List<SubscribedStreamsMiddleware> =
        listOf(SubscribedStreamsMiddleware(repository))

    @Provides
    @StreamsScope
    fun provideAllStreamsMiddlewares(repository: Repository): List<AllStreamsMiddleware> =
        listOf(AllStreamsMiddleware(repository))

    @Provides
    @StreamsScope
    fun provideInitialState(): StreamsUiState = StreamsUiState()

    @Provides
    fun provideActions(): PublishRelay<StreamsActions> = PublishRelay.create()

    @Provides
    @StreamsScope
    fun provideDiffCallBack(): DiffCallback<ViewTyped> = DiffCallback()

    @Provides
    fun provideHolderFactory(): HolderFactory = MainHolderFactory()

    @Provides
    @StreamsScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()
}