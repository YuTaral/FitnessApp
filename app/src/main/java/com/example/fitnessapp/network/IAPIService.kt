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

    @POST("user/change-password")
    fun changePassword(@Body params: Map<String, String>): Call<CustomResponse>

    @POST("user/validate-token")
    fun validateToken(@Body params: Map<String, String>): Call<CustomResponse>

    /** USER PROFILE POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST("user-profile/update-default-values")
    fun updateUserDefaultValues(@Body params: Map<String, String>): Call<CustomResponse>

    /** USER PROFILE GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET("user-profile/get-weight-units")
    fun getWeightUnits(): Call<CustomResponse>

    @GET("user-profile/get-user-default-values")
    fun getUserDefaultValues(@Query("mgExerciseId") workoutId: Long): Call<CustomResponse>

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
    @POST("exercise/add-to-workout")
    fun addExerciseToWorkout(@Body params: Map<String, String>): Call<CustomResponse>

    @POST("exercise/update-exercise-from-workout")
    fun updateExerciseFromWorkout(@Body params: Map<String, String>): Call<CustomResponse>

    @POST("exercise/delete-exercise-from-workout")
    fun deleteExerciseFromWorkout(@Query("exerciseId") params: Long): Call<CustomResponse>

    @POST("exercise/add")
    fun addExercise(@Body params: Map<String, String>): Call<CustomResponse>

    @POST("exercise/update")
    fun updateExercise(@Body params: Map<String, String>): Call<CustomResponse>

    @POST("exercise/delete")
    fun deleteExercise(@Query("MGExerciseId") params: Long): Call<CustomResponse>

    /** EXERCISE GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET("exercise/get-by-mg-id")
    fun getExerciseByMGId(@Query("muscleGroupId") muscleGroupId: Long, @Query("onlyForUser") onlyForUser: String): Call<CustomResponse>

    /** MUSCLE GROUPS GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET("muscle-group/get-by-user")
    fun getMuscleGroups(): Call<CustomResponse>

    /** WORKOUT TEMPLATES POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST("workout-template/add")
    fun addWorkoutTemplate(@Body params: Map<String, String>): Call<CustomResponse>

    @POST("workout-template/delete")
    fun deleteWorkoutTemplate(@Query("templateId") params: Long): Call<CustomResponse>

    /** WORKOUT TEMPLATES GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET("workout-template/get-templates")
    fun getWorkoutTemplates(): Call<CustomResponse>
}
