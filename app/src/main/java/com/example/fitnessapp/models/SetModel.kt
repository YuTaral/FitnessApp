package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** SetModel class representing an exercise set.
 *  Must correspond with server-side SetModel
 */
class SetModel {
    @SerializedName("Id")
    val id: Long

    @SerializedName("Reps")
    val reps: Int

    @SerializedName("Weight")
    val weight: Double

    @SerializedName("Completed")
    var completed: Boolean

    /** Constructor to accept serialized object
     * @param data serialized SetModel object
     */
    constructor(data: String) {
        val gson = Gson()
        val set: SetModel = gson.fromJson(data, SetModel::class.java)

        id = set.id
        reps = set.reps
        weight = set.weight
        completed = set.completed
    }

    /** Empty constructor */
    constructor() {
        id = 0
        reps = 0
        weight = 0.0
        completed = false
    }

    /** Constructor to accept reps and weight
     * @param idVal the id value.
     * @param repsVal the repetitions value
     * @param weightVal the weight value
     * @param completedVal the completed value
     */
    constructor(idVal: Long, repsVal: Int, weightVal: Double, completedVal: Boolean) {
        id = idVal
        reps = repsVal
        weight = weightVal
        completed = completedVal
    }
}