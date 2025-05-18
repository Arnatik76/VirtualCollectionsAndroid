package com.example.finalproject.api

import com.example.finalproject.utils.AuthTokenProvider
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = AuthTokenProvider.getToken()

        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/json")
            .method(originalRequest.method, originalRequest.body)

        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}