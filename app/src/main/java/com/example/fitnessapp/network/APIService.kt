package com.example.fitnessapp.network

import com.example.fitnessapp.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Singleton object object to implement IAPIService interface */
object APIService {
    var instance: IAPIService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        instance = retrofit.create(IAPIService::class.java)
    }

    /**
     * Updates the API service to include the Authorization header with the JWT token if requested.
     * @param token - the token, may be empty (in case of logout)
     */
    fun updateToken(token: String) {
        val service: Retrofit

        if (token != "") {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor(token))
                .build()

            service = Retrofit.Builder()
                .baseUrl(Constants.URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        } else {
            service = Retrofit.Builder()
                .baseUrl(Constants.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        instance = service.create(IAPIService::class.java)
    }
}