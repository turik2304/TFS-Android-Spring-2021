package com.turik2304.coursework.di.modules.app_modules

import com.turik2304.coursework.data.network.ZulipApi
import com.turik2304.coursework.data.network.utils.*
import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.data.room.Database
import com.turik2304.coursework.di.scopes.AppScope
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class RepositoryModule {

    @Provides
    @AppScope
    fun provideRepository(
        api: ZulipApi,
        db: Database,
        viewTypedConverter: ViewTypedConverter,
        narrowConstructor: NarrowConstructor
    ): Repository =
        ZulipRepository(api, db, viewTypedConverter, narrowConstructor)

    @Provides
    @AppScope
    fun provideApi(retrofitClient: Retrofit): ZulipApi = retrofitClient.create(ZulipApi::class.java)

    @Provides
    @AppScope
    fun provideViewTypedConverter(messageHelper: MessageHelper): ViewTypedConverter =
        ViewTypedConverterImpl(messageHelper)

    @Provides
    @AppScope
    fun provideMessageHelper(reactionHelper: ReactionHelper): MessageHelper =
        MessageHelperImpl(reactionHelper)

    @Provides
    @AppScope
    fun provideReactionHelper(): ReactionHelper = ReactionHelperImpl()

    @Provides
    @AppScope
    fun provideNarrowConstructor(): NarrowConstructor = NarrowConstructorImpl()
}