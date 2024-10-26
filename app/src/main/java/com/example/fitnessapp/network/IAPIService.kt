package com.example.fitnessapp.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** Interface to define request end points */
interface IAPIService {

    /** USER  POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST("user/login")
    fun login(@Body params: Map<String, String>): Call<CustomResponse>
    @POST("user/register")
    fun register(@Body params: Map<String, String>): Call<CustomResponse>
    @POST("user/logout")
    fun logout(): Call<CustomResponse>

    /** WORKOUT POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST("workout/add")
    fun addWorkout(@Body params: Map<String, String>): Call<CustomResponse>
    @POST("workout/edit")
    fun editWorkout(@Body params: Map<String, String>): Call<CustomResponse>
    @POST("workout/delete")
    fun deleteWorkout(@Query("workoutId") workoutId: Long): Call<CustomResponse>

    /** WORKOUT GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET("workout/get-workouts")
    fun getWorkouts(): Call<CustomResponse>
    @GET("workout/get-workout")
    fun getWorkout(@Query("workoutId") workoutId: Long): Call<CustomResponse>

    /** EXERCISE POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST("exercise/add")
    fun addExercise(@Body params: Map<String, String>): Call<CustomResponse>
    @POST("exercise/update")
    fun updateExercise(@Body params: Map<String, String>): Call<CustomResponse>
    @POST("exercise/delete")
    fun deleteExercise(@Query("exerciseId") params: Long): Call<CustomResponse>

    /** MUSCLE GROUPS GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET("mgroup/get-muscle-groups")
    fun getMuscleGroups(): Call<CustomResponse>

    /** WORKOUT TEMPLATES POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST("workout-template/add")
    fun addWorkoutTemplate(@Body params: Map<String, String>): Call<CustomResponse>

    /** WORKOUT TEMPLATES GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET("workout-template/get-templates")
    fun getWorkoutTemplates(): Call<CustomResponse>
}
