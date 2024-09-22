package com.example.fitnessapp.utils

/** Object to hold all constants. Must correspond with server side Constants static class */
object Constants {
    const val URL = "http://192.168.0.6:5176/api/"

    /** Enum to hold all response codes defined server-side */
    enum class ResponseCode {
        SUCCESS,
        FAIL,
        UNEXPECTED_ERROR
    }

}