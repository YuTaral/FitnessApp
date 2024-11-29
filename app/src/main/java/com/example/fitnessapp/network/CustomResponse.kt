package com.example.fitnessapp.network

/** CustomResponse class to define the fields which are returned on request response */
data class CustomResponse(
    val responseCode: Int,
    val responseMessage: String,
    val responseData: List<String>,
)