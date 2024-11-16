package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** MuscleGroupModel class representing a muscle group.
 *  Must correspond with server-side MuscleGroupModel
 */
class MuscleGroupModel {
    @SerializedName("Id")
    val id: Long

    @SerializedName("Name")
    val name: String

    @SerializedName("ImageName")
    var imageName: String

    /** Init method to accept serialized object and set the values */
    constructor(data: String) {
        val gson = Gson()
        val model: MuscleGroupModel = gson.fromJson(data, MuscleGroupModel::class.java)

        id = model.id
        name = model.name
        imageName = model.imageName
    }

    constructor(idVal: Long) {
        id = idVal
        name = "Unknown"
        imageName = ""
    }

    constructor() {
        id = 0
        name = "Unknown"
        imageName = ""
    }
}