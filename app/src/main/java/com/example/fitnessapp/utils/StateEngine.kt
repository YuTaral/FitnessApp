package com.example.fitnessapp.utils

import androidx.activity.ComponentActivity
import com.example.fitnessapp.models.UserModel
import com.example.fitnessapp.models.WorkoutModel

/** Object to define the current state (holds current activity, logged in user, etc... */
object StateEngine {
    lateinit var activeActivity: ComponentActivity
    lateinit var user: UserModel
    var workout: WorkoutModel? = null
}