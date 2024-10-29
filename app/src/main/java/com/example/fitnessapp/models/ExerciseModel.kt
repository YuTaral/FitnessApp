package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** ExerciseModel class representing an exercise.
 * Must correspond with server-side ExerciseModel
 */
class ExerciseModel {
    @SerializedName("Id")
    val id: Long

    @SerializedName("Name")
    val name: String

    @SerializedName("Sets")
    val sets: MutableList<SetModel>

    /** Constructor used when new exercise is created
     * @param nameVal the exercise name
     * @param setsCountVal the sets value
     * @param repsVal the repetitions value
     * @param weightVal the weight value
     */
    constructor(nameVal: String, setsCountVal: Int, repsVal: Int, weightVal: Double) {
        id = 0
        name = nameVal
        sets = mutableListOf()

        for (i in 0..<setsCountVal) {
            sets.add(i, SetModel(0, repsVal, weightVal, false))
        }
    }

    /** Constructor used when exercise is updated
     * @param nameVal the exercise name
     * @param setsVal the sets value
     */
    constructor(idVal:Long, nameVal: String, setsVal: MutableList<SetModel>) {
        id = idVal
        name = nameVal
        sets = setsVal
    }
}