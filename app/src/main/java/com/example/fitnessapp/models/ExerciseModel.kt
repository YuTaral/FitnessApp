package com.example.fitnessapp.models

import com.google.gson.annotations.SerializedName

/** ExerciseModel class representing an exercise.
 * Must correspond with server-side ExerciseModel
 */
class ExerciseModel {
    @SerializedName("Id")
    val id: Long

    @SerializedName("Name")
    val name: String

    @SerializedName("MuscleGroup")
    val muscleGroup: MuscleGroupModel

    @SerializedName("Sets")
    val sets: MutableList<SetModel>

    /** Constructor used when new exercise is created
     * @param nameVal the exercise name
     * @param muscleGroupVal the muscle group
     * @param setsCountVal the sets value
     * @param repsVal the repetitions value
     * @param weightVal the weight value
     * @param completedVal the completed value
     */
    constructor(nameVal: String, muscleGroupVal: MuscleGroupModel, setsCountVal: Int, repsVal: Int, weightVal: Double, completedVal: Boolean) {
        id = 0
        name = nameVal
        muscleGroup = muscleGroupVal
        sets = mutableListOf()

        for (i in 0..<setsCountVal) {
            sets.add(i, SetModel(0, repsVal, weightVal, completedVal))
        }
    }

    /** Constructor used when exercise is updated
     * @param nameVal the exercise name
     * @param muscleGroupVal the muscle group
     * @param setsVal the sets value
     */
    constructor(idVal:Long, nameVal:  String, muscleGroupVal: MuscleGroupModel, setsVal: MutableList<SetModel>) {
        id = idVal
        name = nameVal
        muscleGroup = muscleGroupVal
        sets = setsVal
    }
}