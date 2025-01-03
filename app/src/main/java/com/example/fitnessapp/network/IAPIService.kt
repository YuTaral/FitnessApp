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

    @POST(Constants.RequestEndPoints.UPDATE_USER_PROFILE)
    fun updateUserProfile(@Body params: Map<String, String>): Call<CustomResponse>

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
    fun getWorkouts(@Query("filterBy") filterBy: String): Call<CustomResponse>

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
    fun deleteExerciseFromWorkout(@Query("exerciseId") exerciseId: Long): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.ADD_EXERCISE)
    fun addExercise(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.UPDATE_EXERCISE)
    fun updateExercise(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.DELETE_EXERCISE)
    fun deleteExercise(@Query("MGExerciseId") MGExerciseId: Long): Call<CustomResponse>

    /** EXERCISE GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET(Constants.RequestEndPoints.GET_EXERCISES_FOR_MG)
    fun getExerciseByMGId(@Query("muscleGroupId") muscleGroupId: Long, @Query("onlyForUser") onlyForUser: String): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.GET_MG_EXERCISE)
    fun getMGExercise(@Query("mGExerciseId") mGExerciseId: Long): Call<CustomResponse>

    /** MUSCLE GROUPS GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET(Constants.RequestEndPoints.GET_MUSCLE_GROUPS_FOR_USER)
    fun getMuscleGroups(): Call<CustomResponse>

    /** WORKOUT TEMPLATES POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.ADD_WORKOUT_TEMPLATE)
    fun addWorkoutTemplate(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.DELETE_WORKOUT_TEMPLATE)
    fun deleteWorkoutTemplate(@Query("templateId") teamId: Long): Call<CustomResponse>

    /** WORKOUT TEMPLATES GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET(Constants.RequestEndPoints.GET_WORKOUT_TEMPLATES)
    fun getWorkoutTemplates(): Call<CustomResponse>

    /** TEAM POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.ADD_TEAM)
    fun addTeam(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.UPDATE_TEAM)
    fun updateTeam(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.DELETE_TEAM)
    fun deleteTeam(@Query("teamId") teamId: Long): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.INVITE_MEMBER)
    fun inviteMember(@Query("userId") userId: String, @Query("teamId") teamId: Long): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.REMOVE_MEMBER)
    fun removeMember(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.ACCEPT_TEAM_INVITE)
    fun acceptInvite(@Query("userId") userId: String, @Query("teamId") teamId: Long): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.DECLINE_TEAM_INVITE)
    fun declineInvite(@Query("userId") userId: String, @Query("teamId") teamId: Long): Call<CustomResponse>

    /** TEAM GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET(Constants.RequestEndPoints.GET_MY_TEAMS)
    fun getMyTeams(): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.GET_USERS_TO_INVITE)
    fun getUsersToInvite(@Query("name") name: String, @Query("teamId") teamId: Long): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.GET_TEAM_MEMBERS)
    fun getTeamMembers(@Query("teamId") teamId: Long): Call<CustomResponse>

    /** NOTIFICATION POST REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.NOTIFICATION_REVIEWED)
    fun notificationReviewed(@Query("id") id: Long): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.DELETE_NOTIFICATION)
    fun deleteNotification(@Body params: Map<String, String>): Call<CustomResponse>

    /** NOTIFICATION GET REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET(Constants.RequestEndPoints.GET_NOTIFICATIONS)
    fun getNotifications(): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.GET_JOIN_TEAM_NOTIFICATION_DETAILS)
    fun getJoinTeamNotificationDetails(@Query("id") id: Long): Call<CustomResponse>
}
