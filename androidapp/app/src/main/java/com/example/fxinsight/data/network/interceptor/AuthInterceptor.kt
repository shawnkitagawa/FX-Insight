package com.example.fxinsight.data.network.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor (
    private val getToken: () -> String?
    ): Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = getToken()

        Log.d("TOKEN", token ?:"NULL")

        val requestBuilder = chain.request().newBuilder()

        if (token!= null)
        {
            requestBuilder.addHeader("Authorization", "Bearer ${token.trim()}")
        }

        val finalRequest = requestBuilder.build()
        Log.d("HTTP_URL", finalRequest.url.toString())
        return chain.proceed(finalRequest)
    }
}