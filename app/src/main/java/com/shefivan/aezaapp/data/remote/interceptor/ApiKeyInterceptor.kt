package com.shefivan.aezaapp.data.remote.interceptor

import com.shefivan.aezaapp.data.local.ApiKeyProvider
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor @Inject constructor(
    private val apiKeyProvider: ApiKeyProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = apiKeyProvider.get()
        val originalRequest = chain.request()

        if (apiKey.isNullOrBlank()) {
            return chain.proceed(originalRequest)
        }

        val request = originalRequest.newBuilder()
            .header(API_KEY_HEADER, apiKey)
            .build()

        return chain.proceed(request)
    }

    private companion object {
        const val API_KEY_HEADER = "X-API-KEY"
    }
}
