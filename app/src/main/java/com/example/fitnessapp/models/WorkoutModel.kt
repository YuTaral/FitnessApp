package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.Date

/** WorkoutModel class representing an exercise.
 * Must correspond with server-side WorkoutModel
 */
class WorkoutModel {
    @SerializedName("Id")
    val id: Long

    @SerializedName("Name")
    var name: String

    @SerializedName("Date")
    val date: Date

    @SerializedName("Template")
    val template: Boolean

    @SerializedName("Exercises")
    val exercises: MutableList<ExerciseModel>

    /** Constructor to accept serialized object
     * @param data serialized WorkoutModel object
     */
    constructor(data: String) {
        val gson = Gson()
        val workout: WorkoutModel = gson.fromJson(data, WorkoutModel::class.java)

        id = workout.id
        name = workout.name
        date = workout.date
        template = workout.template
        exercises = workout.exercises
    }

    /** Constructor used when new workout is created */
    constructor(idVal: Long, nameVal: String, templateVal: Boolean, exercisesVal: MutableList<ExerciseModel>) {
        id = idVal
        name = nameVal
        date = Date()
        template = templateVal
        exercises = exercisesVal
    }
}