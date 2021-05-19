package com.turik2304.coursework.di.modules.app_modules

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.turik2304.coursework.data.network.ZulipApi
import com.turik2304.coursework.data.network.utils.*
import com.turik2304.coursework.data.repository.Repository
import com.turik2304.coursework.data.repository.ZulipRepository
import com.turik2304.coursework.data.room.Database
import com.turik2304.coursework.di.scopes.AppScope
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
class AppModule(val application: Application) {

    @Provides
    @AppScope
    fun provideContext(): Context = application
}