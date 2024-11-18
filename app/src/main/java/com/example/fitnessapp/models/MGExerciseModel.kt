package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** MGExerciseModel class representing an exercise of specific muscle group.
 * Must correspond with server-side MGExerciseModel
 */
class MGExerciseModel: BaseModel {
    @SerializedName("Name")
    val name: String

    @SerializedName("Description")
    val description: String

    @SerializedName("MuscleGroupId")
    val muscleGroupId: Long


    /** Constructor to deserialized MGExerciseModel object
     * @param data serialized MGExerciseModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: MGExerciseModel = gson.fromJson(data, MGExerciseModel::class.java)

        name = model.name
        description = model.description
        muscleGroupId = model.muscleGroupId
    }


    /** Constructor used when new MGExerciseModel object is created
     * @param idVal the id
     * @param nameVal the exercise name
     * @param descriptionVal the description
     * @param muscleGroupIdVal the muscle group id
     */
    constructor(idVal: Long, nameVal: String, descriptionVal: String, muscleGroupIdVal: Long) : super(idVal) {
        name = nameVal
        description = descriptionVal
        muscleGroupId = muscleGroupIdVal
    }
}