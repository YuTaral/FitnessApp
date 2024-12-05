package com.example.fitnessapp.network

import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Singleton object to implement IAPIService interface */
object APIService {
    var instance: IAPIService

    init {
        // Create the instance with update token. If the token is already stored, it will create instance
        // attaching the token to the authorization header
        instance = updateToken(Utils.getStoredToken())
    }

    /**
     * Update the API service to include the Authorization header with the JWT token if requested.
     * @param token - the token, may be empty (in case of logout)
     */
    fun updateToken(token: String): IAPIService {
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

        // Update the token in shared prefs
        Utils.updateTokenInPrefs(token)

        return instance
    }
}