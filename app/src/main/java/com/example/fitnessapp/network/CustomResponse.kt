package com.example.fitnessapp.network

/** CustomResponse class to define the fields which are returned on request response */
data class CustomResponse(
    val code: Int,
    val message: String,
    val data: List<String>,
)