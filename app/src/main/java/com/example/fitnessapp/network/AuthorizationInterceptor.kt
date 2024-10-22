package com.example.fitnessapp.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


/**
 * Interceptor to add an Authorization header to every HTTP request made by OkHttp.
 * @property token The JWT token to be added as the Authorization header in the request.
 */
class AuthorizationInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        // Add the Authorization header with the JWT token if it's available
        val requestWithToken = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(requestWithToken)
    }
}
