package com.example.fitnessapp.network

/** Data class to define the fields which are returned after request */
data class CustomResponse(
    val responseCode: Int,
    val userMessage: String,
    val returnData: List<String>,
)