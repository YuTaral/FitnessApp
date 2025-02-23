package com.example.fitnessapp.network

import com.example.fitnessapp.utils.Constants
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

/** Interface to define request end points */
interface IAPIService {

    /** USER REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.LOGIN)
    fun login(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.REGISTER)
    fun register(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.LOGOUT)
    fun logout(): Call<CustomResponse>

    @PUT(Constants.RequestEndPoints.CHANGE_PASSWORD)
    fun changePassword(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.VALIDATE_TOKEN)
    fun validateToken(@Body params: Map<String, String>): Call<CustomResponse>

    /** USER PROFILE REQUESTS
     * -------------------------------------------------------------------------------- */
    @PATCH(Constants.RequestEndPoints.DEFAULT_VALUES)
    fun updateUserDefaultValues(@Body params: Map<String, String>): Call<CustomResponse>

    @PATCH(Constants.RequestEndPoints.USER_PROFILES)
    fun updateUserProfile(@Body params: Map<String, String>): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.DEFAULT_VALUES)
    fun getUserDefaultValues(@Query("mgExerciseId") workoutId: Long): Call<CustomResponse>

    /** WORKOUT REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.WORKOUTS)
    fun addWorkout(@Body params: Map<String, String>): Call<CustomResponse>

    @PATCH(Constants.RequestEndPoints.WORKOUTS)
    fun updateWorkout(@Body params: Map<String, String>): Call<CustomResponse>

    @DELETE(Constants.RequestEndPoints.WORKOUTS)
    fun deleteWorkout(@Query("workoutId") workoutId: Long): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.WORKOUTS)
    fun getWorkouts(@Query("startDate") startDate: String): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.WEIGHT_UNITS)
    fun getWeightUnits(): Call<CustomResponse>

    /** EXERCISE REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.TO_WORKOUT)
    fun addExerciseToWorkout(@Body params: Map<String, String>): Call<CustomResponse>

    @PATCH(Constants.RequestEndPoints.EXERCISE_FROM_WORKOUT)
    fun updateExerciseFromWorkout(@Body params: Map<String, String>): Call<CustomResponse>

    @DELETE(Constants.RequestEndPoints.EXERCISE_FROM_WORKOUT)
    fun deleteExerciseFromWorkout(@Query("exerciseId") exerciseId: Long): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.EXERCISES)
    fun addExercise(@Body params: Map<String, String>): Call<CustomResponse>

    @PATCH(Constants.RequestEndPoints.EXERCISES)
    fun updateExercise(@Body params: Map<String, String>): Call<CustomResponse>

    @DELETE(Constants.RequestEndPoints.EXERCISES)
    fun deleteExercise(@Query("MGExerciseId") MGExerciseId: Long): Call<CustomResponse>

    @PATCH(Constants.RequestEndPoints.COMPLETE_SET)
    fun completeSet(@Body params: Map<String, String>): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.EXERCISES_FOR_MG)
    fun getExerciseByMGId(@Query("muscleGroupId") muscleGroupId: Long, @Query("onlyForUser") onlyForUser: String): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.MG_EXERCISE)
    fun getMGExercise(@Query("mGExerciseId") mGExerciseId: Long): Call<CustomResponse>

    /** MUSCLE GROUPS REQUESTS
     * -------------------------------------------------------------------------------- */
    @GET(Constants.RequestEndPoints.MUSCLE_GROUPS)
    fun getMuscleGroups(): Call<CustomResponse>

    /** WORKOUT TEMPLATES REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.WORKOUT_TEMPLATES)
    fun addWorkoutTemplate(@Body params: Map<String, String>): Call<CustomResponse>

    @PATCH(Constants.RequestEndPoints.WORKOUT_TEMPLATES)
    fun updateWorkoutTemplate(@Body params: Map<String, String>): Call<CustomResponse>

    @DELETE(Constants.RequestEndPoints.WORKOUT_TEMPLATES)
    fun deleteWorkoutTemplate(@Query("templateId") teamId: Long): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.WORKOUT_TEMPLATES)
    fun getWorkoutTemplates(): Call<CustomResponse>

    /** TEAM REQUESTS
     * -------------------------------------------------------------------------------- */
    @POST(Constants.RequestEndPoints.TEAMS)
    fun addTeam(@Body params: Map<String, String>): Call<CustomResponse>

    @PATCH(Constants.RequestEndPoints.TEAMS)
    fun updateTeam(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.TEAMS)
    fun deleteTeam(@Query("teamId") teamId: Long): Call<CustomResponse>

    @PUT(Constants.RequestEndPoints.LEAVE_TEAM)
    fun leaveTeam(@Body params: Map<String, String>): Call<CustomResponse>

    @POST(Constants.RequestEndPoints.INVITE_MEMBER)
    fun inviteMember(@Body params: Map<String, String>): Call<CustomResponse>

    @PATCH(Constants.RequestEndPoints.REMOVE_MEMBER)
    fun removeMember(@Body params: Map<String, String>): Call<CustomResponse>

    @PATCH(Constants.RequestEndPoints.ACCEPT_TEAM_INVITE)
    fun acceptInvite(@Body params: Map<String, String>): Call<CustomResponse>

    @PATCH(Constants.RequestEndPoints.DECLINE_TEAM_INVITE)
    fun declineInvite(@Body params: Map<String, String>): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.MY_TEAMS)
    fun getMyTeams(@Query("teamType") teamType: String): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.MY_TEAMS_WITH_MEMBERS)
    fun getMyTeamsWithMembers(): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.USERS_TO_INVITE)
    fun getUsersToInvite(@Query("name") name: String, @Query("teamId") teamId: Long): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.MY_TEAM_MEMBERS)
    fun getTeamMembers(@Query("teamId") teamId: Long): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.JOINED_TEAM_MEMBERS)
    fun getJoinedTeamMembers(@Query("teamId") teamId: Long): Call<CustomResponse>

    /** NOTIFICATION REQUESTS
     * -------------------------------------------------------------------------------- */
    @PATCH(Constants.RequestEndPoints.NOTIFICATIONS)
    fun notificationReviewed(@Body params: Map<String, String>): Call<CustomResponse>

    @DELETE(Constants.RequestEndPoints.NOTIFICATIONS)
    fun deleteNotification(@Query("notificationId") notificationId: Long): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.NOTIFICATIONS)
    fun getNotifications(): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.JOIN_TEAM_NOTIFICATION_DETAILS)
    fun getJoinTeamNotificationDetails(@Query("notificationId") notificationId: Long): Call<CustomResponse>

    @GET(Constants.RequestEndPoints.REFRESH_NOTIFICATIONS)
    fun refreshNotifications(): Call<CustomResponse>
}
