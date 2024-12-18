package com.example.fitnessapp.utils


import android.annotation.SuppressLint
import com.example.fitnessapp.BuildConfig

/** Object to hold all constants. Must correspond with server side Constants static class */
object Constants {
    const val URL: String = BuildConfig.BASE_URL

    /** Enum to hold all response codes defined server-side */
    enum class ResponseCode {
        SUCCESS,
        FAIL,
        UNEXPECTED_ERROR,
        EXERCISE_ALREADY_EXISTS,
        TOKEN_EXPIRED,
        REFRESH_TOKEN
    }

    /** Enum to hold the panel unique ids */
    enum class PanelUniqueId {
        MAIN,
        WORKOUT,
        TEMPLATES,
        SELECT_EXERCISE,
        MANAGE_EXERCISE
    }

    /** Object containing request end point values */
    object RequestEndPoints {
        private const val USER = "user"
        private const val WORKOUT = "workout"
        private const val EXERCISE = "exercise"
        private const val MUSCLE_GROUP = "muscle-group"
        private const val USER_PROFILE = "user-profile"
        private const val WORKOUT_TEMPLATE = "workout-template"

        const val REGISTER = "$USER/register"
        const val LOGIN = "$USER/login"
        const val LOGOUT = "$USER/logout"
        const val CHANGE_PASSWORD = "$USER/change-password"
        const val VALIDATE_TOKEN = "$USER/validate-token"

        const val ADD_WORKOUT = "$WORKOUT/add"
        const val UPDATE_WORKOUT = "$WORKOUT/update"
        const val DELETE_WORKOUT = "$WORKOUT/delete"
        const val GET_WORKOUTS = "$WORKOUT/get-workouts"
        const val GET_WORKOUT = "$WORKOUT/get-workout"
        const val GET_WEIGHT_UNITS  = "$WORKOUT/get-weight-units"

        const val ADD_EXERCISE_TO_WORKOUT = "$EXERCISE/add-to-workout"
        const val UPDATE_EXERCISE_FROM_WORKOUT = "$EXERCISE/update-exercise-from-workout"
        const val DELETE_EXERCISE_FROM_WORKOUT = "$EXERCISE/delete-exercise-from-workout"
        const val ADD_EXERCISE = "$EXERCISE/add"
        const val UPDATE_EXERCISE = "$EXERCISE/update"
        const val DELETE_EXERCISE = "$EXERCISE/delete"
        const val GET_EXERCISES_FOR_MG = "$EXERCISE/get-by-mg-id"

        const val GET_MUSCLE_GROUPS_FOR_USER = "$MUSCLE_GROUP/get-by-user"

        const val UPDATE_USER_DEFAULT_VALUES = "$USER_PROFILE/update-default-values"
        const val UPDATE_USER_PROFILE = "$USER_PROFILE/update-profile"
        const val GET_USER_DEFAULT_VALUES = "$USER_PROFILE/get-default-values"

        const val ADD_WORKOUT_TEMPLATE  = "$WORKOUT_TEMPLATE/add"
        const val DELETE_WORKOUT_TEMPLATE  = "$WORKOUT_TEMPLATE/delete"
        const val GET_WORKOUT_TEMPLATES  = "$WORKOUT_TEMPLATE/get-templates"
    }

    /** Object containing request permission strings */
    object Permissions {
        const val CAMERA = android.Manifest.permission.CAMERA

        @SuppressLint("InlinedApi")
        const val READ_MEDIA_IMAGES = android.Manifest.permission.READ_MEDIA_IMAGES

        const val READ_EXTERNAL_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
    }
}