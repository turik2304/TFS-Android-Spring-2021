package com.turik2304.coursework.network

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkClient {

    val okHttpClient: OkHttpClient
        get() = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .build()

    private val interceptor = Interceptor { chain ->
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header(
                "Authorization",
                Credentials.basic("asibag98@gmail.com", "fjMrYYPpJBw87hculEvh47Ckc7eW08yN")
            ).build()
        chain.proceed(authenticatedRequest)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
}