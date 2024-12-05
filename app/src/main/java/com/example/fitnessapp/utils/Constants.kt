package com.example.fitnessapp.utils


import com.example.fitnessapp.BuildConfig

/** Object to hold all constants. Must correspond with server side Constants static class */
object Constants {
    var URL: String = BuildConfig.BASE_URL

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
}