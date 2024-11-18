package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.Date

/** WorkoutModel class representing an exercise.
 * Must correspond with server-side WorkoutModel
 */
class WorkoutModel: BaseModel {
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
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: WorkoutModel = gson.fromJson(data, WorkoutModel::class.java)

        name = model.name
        date = model.date
        template = model.template
        exercises = model.exercises
    }

    /** Constructor used when new workout is created
     * @param idVal the id
     * @param nameVal the name
     * @param templateVal "Y" if the workout is template, "N" otherwise
     * @param exercisesVal the exercises for this workout
     */
    constructor(idVal: Long, nameVal: String, templateVal: Boolean, exercisesVal: MutableList<ExerciseModel>) : super (idVal) {
        name = nameVal
        date = Date()
        template = templateVal
        exercises = exercisesVal
    }
}