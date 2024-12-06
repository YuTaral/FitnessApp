package com.example.fitnessapp.network

import com.example.fitnessapp.utils.Constants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/** Interface to define request end points */
interface IAPIService {

    /** USER  POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.LOGIN)
    fun login(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.REGISTER)
    fun register(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.LOGOUT)
    fun logout(): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.CHANGE_PASSWORD)
    fun changePassword(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.VALIDATE_TOKEN)
    fun validateToken(@Body params: Map<String, String>): Call<CustomResponse>

    /** USER PROFILE POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.UPDATE_USER_DEFAULT_VALUES)
    fun updateUserDefaultValues(@Body params: Map<String, String>): Call<CustomResponse>

    /** USER PROFILE GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET(Constants.RequestEndPoints.GET_USER_DEFAULT_VALUES)
    fun getUserDefaultValues(@Query("mgExerciseId") workoutId: Long): Call<CustomResponse>

    /** WORKOUT POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.ADD_WORKOUT)
    fun addWorkout(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.UPDATE_WORKOUT)
    fun updateWorkout(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.DELETE_WORKOUT)
    fun deleteWorkout(@Query("workoutId") workoutId: Long): Call<CustomResponse>

    /** WORKOUT GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET(Constants.RequestEndPoints.GET_WORKOUTS)
    fun getWorkouts(): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.GET_WORKOUT)
    fun getWorkout(@Query("workoutId") workoutId: Long): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.GET_WEIGHT_UNITS)
    fun getWeightUnits(): Call<CustomResponse>

    /** EXERCISE POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.ADD_EXERCISE_TO_WORKOUT)
    fun addExerciseToWorkout(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.UPDATE_EXERCISE_FROM_WORKOUT)
    fun updateExerciseFromWorkout(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.DELETE_EXERCISE_FROM_WORKOUT)
    fun deleteExerciseFromWorkout(@Query("exerciseId") params: Long): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.ADD_EXERCISE)
    fun addExercise(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.UPDATE_EXERCISE)
    fun updateExercise(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.DELETE_EXERCISE)
    fun deleteExercise(@Query("MGExerciseId") params: Long): Call<CustomResponse>

    /** EXERCISE GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET(Constants.RequestEndPoints.GET_EXERCISES_FOR_MG)
    fun getExerciseByMGId(@Query("muscleGroupId") muscleGroupId: Long, @Query("onlyForUser") onlyForUser: String): Call<CustomResponse>

    /** MUSCLE GROUPS GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET(Constants.RequestEndPoints.GET_MUSCLE_GROUPS_FOR_USER)
    fun getMuscleGroups(): Call<CustomResponse>

    /** WORKOUT TEMPLATES POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.ADD_WORKOUT_TEMPLATE)
    fun addWorkoutTemplate(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.DELETE_WORKOUT_TEMPLATE)
    fun deleteWorkoutTemplate(@Query("templateId") params: Long): Call<CustomResponse>

    /** WORKOUT TEMPLATES GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET(Constants.RequestEndPoints.GET_WORKOUT_TEMPLATES)
    fun getWorkoutTemplates(): Call<CustomResponse>
}
