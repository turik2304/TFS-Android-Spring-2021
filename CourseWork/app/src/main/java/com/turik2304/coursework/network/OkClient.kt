package com.turik2304.coursework.network

import okhttp3.*
import java.util.concurrent.TimeUnit

object OkClient {

    val okHttpClient: OkHttpClient
        get() = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
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
}