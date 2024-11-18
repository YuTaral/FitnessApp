package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** SetModel class representing an exercise set.
 *  Must correspond with server-side SetModel
 */
class SetModel: BaseModel {
    @SerializedName("Reps")
    val reps: Int

    @SerializedName("Weight")
    val weight: Double

    @SerializedName("Completed")
    var completed: Boolean

    /** Constructor to accept serialized object
     * @param data serialized SetModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: SetModel = gson.fromJson(data, SetModel::class.java)

        reps = model.reps
        weight = model.weight
        completed = model.completed
    }

    /** Empty constructor */
    constructor() : super(0) {
        reps = 0
        weight = 0.0
        completed = false
    }

    /** Constructor used when new SetModel object is created
     * @param idVal the id value.
     * @param repsVal the repetitions value
     * @param weightVal the weight value
     * @param completedVal the completed value
     */
    constructor(idVal: Long, repsVal: Int, weightVal: Double, completedVal: Boolean) : super(idVal) {
        reps = repsVal
        weight = weightVal
        completed = completedVal
    }
}