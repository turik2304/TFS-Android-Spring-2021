package com.turik2304.coursework.di.modules

import com.jakewharton.rxrelay3.PublishRelay
import com.turik2304.coursework.di.scopes.PeoplesScope
import com.turik2304.coursework.domain.OwnProfileMiddleware
import com.turik2304.coursework.domain.UsersMiddleware
import com.turik2304.coursework.presentation.UsersActions
import com.turik2304.coursework.presentation.UsersReducer
import com.turik2304.coursework.presentation.UsersUiState
import com.turik2304.coursework.presentation.base.Store
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
    ): Store<UsersActions, UsersUiState> {
        return Store(
            reducer = reducer,
            middlewares = middlewares,
            initialState = initialState
        )
    }

    @Provides
    @PeoplesScope
    @Named(OWN_PROFILE_STORE)
    fun provideOwnProfileStore(
        reducer: UsersReducer,
        middlewares: List<OwnProfileMiddleware>,
        initialState: UsersUiState
    ): Store<UsersActions, UsersUiState> {
        return Store(
            reducer = reducer,
            middlewares = middlewares,
            initialState = initialState
        )
    }

    @Provides
    @PeoplesScope
    fun provideReducer(): UsersReducer {
        return UsersReducer()
    }

    @Provides
    @PeoplesScope
    fun provideUsersMiddleware(): List<UsersMiddleware> {
        return listOf(UsersMiddleware())
    }

    @Provides
    @PeoplesScope
    fun provideOwnProfileMiddleware(): List<OwnProfileMiddleware> {
        return listOf(OwnProfileMiddleware())
    }

    @Provides
    @PeoplesScope
    fun provideInitialState(): UsersUiState {
        return UsersUiState()
    }

    @Provides
    @PeoplesScope
    fun provideActions(): PublishRelay<UsersActions> {
        return PublishRelay.create()
    }

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }
}