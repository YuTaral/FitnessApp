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
    val name: String

    @SerializedName("Date")
    val date: Date

    @SerializedName("Exercises")
    val exercises: MutableList<ExerciseModel>

    @SerializedName("MuscleGroups")
    val muscleGroups: MutableList<MuscleGroupModel>

    /** Constructor to accept serialized object
     * @param data serialized WorkoutModel object
     */
    constructor(data: String) {
        val gson = Gson()
        val workout: WorkoutModel = gson.fromJson(data, WorkoutModel::class.java)

        id = workout.id
        name = workout.name
        date = workout.date
        exercises = workout.exercises
        muscleGroups = workout.muscleGroups
    }

    /** Constructor used when new workout is created
     * @param idVal the exercise id
     * @param nameVal the exercise name
     * @param exercisesVal the exercises
     */
    constructor(idVal: Long, nameVal: String, exercisesVal: MutableList<ExerciseModel>, muscleGroupsVal: MutableList<MuscleGroupModel>) {
        id = idVal
        name = nameVal
        date = Date()
        exercises = exercisesVal
        muscleGroups = muscleGroupsVal
    }
}