package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** MGExerciseModel class representing an exercise of specific muscle group.
 * Must correspond with server-side MGExerciseModel
 */
class MGExerciseModel {
    @SerializedName("Id")
    val id: Long

    @SerializedName("Name")
    val name: String

    @SerializedName("Description")
    val description: String

    @SerializedName("MuscleGroupId")
    val muscleGroupId: Long

    constructor(data: String) {
        val gson = Gson()
        val model: MGExerciseModel = gson.fromJson(data, MGExerciseModel::class.java)

        id = model.id
        name = model.name
        description = model.description
        muscleGroupId = model.muscleGroupId
    }

    constructor(idVal: Long, nameVal: String, descriptionVal: String, muscleGroupIdVal: Long) {
        id = idVal
        name = nameVal
        description = descriptionVal
        muscleGroupId = muscleGroupIdVal
    }
}