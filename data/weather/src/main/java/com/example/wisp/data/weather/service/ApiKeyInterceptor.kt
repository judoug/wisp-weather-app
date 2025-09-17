package com.example.wisp.data.weather.service

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

/**
 * OkHttp interceptor that automatically adds the OpenWeather API key to requests.
 * Injects the API key from BuildConfig into the query parameters.
 */
class ApiKeyInterceptor @Inject constructor(
    @Named("openweather_api_key") private val apiKey: String
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        
        // Add API key to query parameters
        val urlWithApiKey = originalUrl.newBuilder()
            .addQueryParameter("appid", apiKey)
            .build()
        
        val newRequest = originalRequest.newBuilder()
            .url(urlWithApiKey)
            .build()
        
        return chain.proceed(newRequest)
    }
}

