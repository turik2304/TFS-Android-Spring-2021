package com.turik2304.coursework.di.modules.app_modules

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.turik2304.coursework.di.scopes.AppScope
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import javax.inject.Named

@Module
class RetrofitModule {

    @Provides
    @AppScope
    fun provideRetrofitClient(
        okHttpClient: OkHttpClient,
        @Named("baseURL") baseURL: String,
        converterFactory: Converter.Factory
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseURL)
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

    @Provides
    @AppScope
    @Named("baseURL")
    fun provideBaseURL(): String = "https://tfs-android-2021-spring.zulipchat.com/api/v1/"

    @Provides
    @AppScope
    fun provideConverterFactory(): Converter.Factory = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }.asConverterFactory("application/json".toMediaType())
}