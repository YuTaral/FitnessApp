package com.example.fitnessapp.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.fitnessapp.models.UserModel
import com.example.fitnessapp.models.WorkoutModel

/** Object to define the current state (holds current activity, logged in user, etc... */
object StateEngine {
    /** The active activity */
    lateinit var activeActivity: AppCompatActivity

    /** The logged in user */
    lateinit var user: UserModel

    /** The selected workout */
    var workout: WorkoutModel? = null

    /** Used to track when change in the selected workout occurred and
     *  latest workouts refresh is needed
     */
    var refreshWorkouts = false

    /** The panels pager */
    lateinit var pager: ViewPager2
}