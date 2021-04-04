package com.turik2304.coursework.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetroClient {

    private const val BASE_URL = "https://tfs-android-2021-spring.zulipchat.com/api/v1/"

    val retrofit: Retrofit
        get() = Retrofit.Builder()
            .client(OkClient.okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(converterFactory)
            .build()

    private val converterFactory = Json {
        ignoreUnknownKeys = true
    }.asConverterFactory("application/json".toMediaType())


}