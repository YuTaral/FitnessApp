package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** SetModel class representing an exercise set.
 *  Must correspond with server-side SetModel,
 *  excluding deletable property which is used only client side
 */
class SetModel: BaseModel {
    @SerializedName("Reps")
    val reps: Int

    @SerializedName("Weight")
    val weight: Double

    @SerializedName("Rest")
    val rest: Int

    @SerializedName("Completed")
    var completed: Boolean

    var deletable: Boolean

    /** Constructor to accept serialized object
     * @param data serialized SetModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: SetModel = gson.fromJson(data, SetModel::class.java)

        reps = model.reps
        weight = model.weight
        rest = model.reps
        completed = model.completed
        deletable = false
    }

    /** Constructor used when new SetModel object is created
     * @param idVal the id value.
     * @param repsVal the repetitions value
     * @param weightVal the weight value
     * @param restVal the rest in seconds
     * @param completedVal the completed value
     */
    constructor(idVal: Long, repsVal: Int, weightVal: Double, restVal: Int, completedVal: Boolean) : super(idVal) {
        reps = repsVal
        weight = weightVal
        rest = restVal
        completed = completedVal
        deletable = false
    }
}