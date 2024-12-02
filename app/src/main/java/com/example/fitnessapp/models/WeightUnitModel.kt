package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** WeightUnitModel class representing an weight unit.
 *  Must correspond with server-side WeightUnitModel
 */
class WeightUnitModel: BaseModel {
    @SerializedName("Text")
    val text: String

    /** Constructor to accept serialized object
     * @param data serialized SetModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: WeightUnitModel = gson.fromJson(data, WeightUnitModel::class.java)

        text = model.text
    }

    /** Empty constructor */
    constructor(): super(0) {
        text = ""
    }
}