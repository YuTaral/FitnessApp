package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** BaseModel parent class for common properties between all models .
 * Must correspond with server-side BaseModel
 */
open class BaseModel {
    @SerializedName("Id")
    var id: Long

    /** Constructor to set the id, extracting the data from the provided string */
    constructor(data: String) {
        val gson = Gson()
        val model: BaseModel = gson.fromJson(data, BaseModel::class.java)

        id = model.id
    }

    /** Constructor to set the id, to the provided value */
    constructor(idVal: Long) {
        id = idVal
    }
}