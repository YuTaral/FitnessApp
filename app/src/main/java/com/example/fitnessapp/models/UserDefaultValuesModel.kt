package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** UserDefaultValuesModel class representing default exercise values the user defined.
 *  Must correspond with server-side UserDefaultValuesModel
 */
class UserDefaultValuesModel: BaseModel {
    @SerializedName("Sets")
    val sets: Int

    @SerializedName("Reps")
    val reps: Int

    @SerializedName("Weight")
    val weight: Double

    @SerializedName("Rest")
    val rest: Int

    @SerializedName("Completed")
    val completed: Boolean

    @SerializedName("WeightUnit")
    val weightUnit: WeightUnitModel

    @SerializedName("MGExerciseId")
    val mGExerciseId: Long

    /** Constructor to accept serialized object
     * @param data serialized SetModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: UserDefaultValuesModel = gson.fromJson(data, UserDefaultValuesModel::class.java)

        sets = model.sets
        reps = model.reps
        weight = model.weight
        rest = model.rest
        completed = model.completed
        weightUnit = model.weightUnit
        mGExerciseId = model.mGExerciseId
    }

    /** Constructor to create new object */
    constructor(idVal: Long, setsVal: Int, repsVal: Int, weightVal: Double, restVal: Int, completedVal: Boolean, weightUnitVal: WeightUnitModel, mGExerciseIdVal: Long): super(idVal) {
        sets = setsVal
        reps = repsVal
        weight = weightVal
        rest = restVal
        completed = completedVal
        weightUnit = weightUnitVal
        mGExerciseId = mGExerciseIdVal
    }
}