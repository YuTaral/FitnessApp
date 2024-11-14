package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** MuscleGroupModel class representing a muscle group.
 *  Must correspond with server-side MuscleGroupModel
 */
class MuscleGroupModel(data: String) {
    @SerializedName("Id")
    val id: Long

    @SerializedName("Name")
    val name: String

    @SerializedName("Checked")
    var checked: Boolean

    @SerializedName("ImageName")
    var imageName: String

    /** Init method to accept serialized object and set the values */
    init {
        val gson = Gson()
        val model: MuscleGroupModel = gson.fromJson(data, MuscleGroupModel::class.java)

        id = model.id
        name = model.name
        checked = model.checked
        imageName = model.imageName
    }
}