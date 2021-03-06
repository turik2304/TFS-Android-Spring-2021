package com.turik2304.coursework.di.modules.app_modules

import com.turik2304.coursework.di.scopes.AppScope
import dagger.Module
import dagger.Provides
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Module
class OkHttpModule {

    @Provides
    @AppScope
    fun provideOkHttpClient(
        interceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(interceptor)
        .addNetworkInterceptor(loggingInterceptor)
        .build()

    @Provides
    @AppScope
    fun provideInterceptor(): Interceptor = Interceptor { chain ->
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header(
                "Authorization",
                Credentials.basic("asibag98@gmail.com", "fjMrYYPpJBw87hculEvh47Ckc7eW08yN")
            ).build()
        chain.proceed(authenticatedRequest)
    }

    @Provides
    @AppScope
    fun provideLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}