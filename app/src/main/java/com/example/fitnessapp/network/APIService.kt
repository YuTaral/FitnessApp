package com.example.fitnessapp.network

import com.example.fitnessapp.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Singleton object object to implement IAPIService interface */
object APIService {
    val instance: IAPIService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        instance = retrofit.create(IAPIService::class.java)
    }
}