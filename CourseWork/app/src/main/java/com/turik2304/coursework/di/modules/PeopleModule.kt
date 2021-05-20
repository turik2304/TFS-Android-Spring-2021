package com.turik2304.coursework.di.modules

import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.di.scopes.PeoplesScope
import com.turik2304.coursework.domain.OwnProfileMiddleware
import com.turik2304.coursework.domain.UsersMiddleware
import com.turik2304.coursework.presentation.UsersActions
import com.turik2304.coursework.presentation.UsersReducer
import com.turik2304.coursework.presentation.UsersUiState
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
class PeopleModule {

    companion object {
        const val PEOPLE_STORE = "PEOPLE"
        const val OWN_PROFILE_STORE = "OWN_PROFILE"
    }

    @Provides
    @PeoplesScope
    @Named(PEOPLE_STORE)
    fun providePeopleStore(
        reducer: UsersReducer,
        middlewares: List<UsersMiddleware>,
        initialState: UsersUiState
    ): Store<UsersActions, UsersUiState> = Store(
        reducer = reducer,
        middlewares = middlewares,
        initialState = initialState
    )

    @Provides
    @PeoplesScope
    @Named(OWN_PROFILE_STORE)
    fun provideOwnProfileStore(
        reducer: UsersReducer,
        middlewares: List<OwnProfileMiddleware>,
        initialState: UsersUiState
    ): Store<UsersActions, UsersUiState> = Store(
        reducer = reducer,
        middlewares = middlewares,
        initialState = initialState
    )

    @Provides
    @PeoplesScope
    fun provideReducer(): UsersReducer = UsersReducer()

    @Provides
    @PeoplesScope
    fun provideUsersMiddleware(repository: Repository): List<UsersMiddleware> =
        listOf(UsersMiddleware(repository))

    @Provides
    @PeoplesScope
    fun provideOwnProfileMiddleware(repository: Repository): List<OwnProfileMiddleware> =
        listOf(OwnProfileMiddleware(repository))

    @Provides
    @PeoplesScope
    fun provideInitialState(): UsersUiState = UsersUiState()

    @Provides
    @PeoplesScope
    fun provideActions(): PublishRelay<UsersActions> = PublishRelay.create()

    @Provides
    fun provideDiffCallBack(): DiffCallback<ViewTyped> = DiffCallback()

    @Provides
    fun provideHolderFactory(): HolderFactory = MainHolderFactory()

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()
}