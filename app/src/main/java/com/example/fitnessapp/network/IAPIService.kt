package com.example.fitnessapp.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

    @POST("workout/delete-exercise")
    fun deleteExercise(@Body params: Long): Call<CustomResponse>

    @GET("workout/get-workouts")
    fun getWorkouts(@Query("userId") userId: String): Call<CustomResponse>
}
