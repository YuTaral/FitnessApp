package com.example.fitnessapp.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/** Interface to define request end points and the data send to the server */
interface IAPIService {
    @POST("user/login")
    fun login(@Body params: Map<String, String>): Call<CustomResponse>

    @POST("user/register")
    fun register(@Body params: Map<String, String>): Call<CustomResponse>

    @POST("workout/add")
    fun addWorkout(@Body params: Map<String, String>): Call<CustomResponse>

    @POST("workout/add-exercise")
    fun addExercise(@Body params: Map<String, String>): Call<CustomResponse>

    @POST("workout/update-exercise")
    fun updateExercise(@Body params: Map<String, String>): Call<CustomResponse>
}
