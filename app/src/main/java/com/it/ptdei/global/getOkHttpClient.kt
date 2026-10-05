package com.it.ptdei.global

import android.util.Log
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

fun getOkHttpClient(
    sessionManager: SessionManager,
): OkHttpClient {

    return OkHttpClient.Builder().readTimeout(300, TimeUnit.SECONDS).addInterceptor { chain ->

        val savedCookie = sessionManager.getCookie()

        Log.d(
            "COOKIE_DEBUG", "Before request cookie = $savedCookie"
        )

        val requestBuilder = chain.request().newBuilder()

        savedCookie?.let {
            requestBuilder.addHeader(
                "Cookie", it
            )
        }

        val response = chain.proceed(requestBuilder.build())

        response.headers("Set-Cookie").firstOrNull()?.let {

            Log.d(
                "COOKIE_DEBUG", "Received cookie = $it"
            )

            sessionManager.saveCookie(it)
        }

        response
    }.build()
}