package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** MuscleGroupModel class representing a muscle group.
 *  Must correspond with server-side MuscleGroupModel
 */
class MuscleGroupModel: BaseModel {

    @SerializedName("Name")
    val name: String

    @SerializedName("ImageName")
    var imageName: String

    /** Constructor to deserialized MuscleGroupMode object
     * @param data serialized MuscleGroupMode object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: MuscleGroupModel = gson.fromJson(data, MuscleGroupModel::class.java)

        name = model.name
        imageName = model.imageName
    }

    /** Constructor to set only the id
     * @param idVal the id
     */
    constructor(idVal: Long) : super(idVal) {
        name = "Unknown"
        imageName = ""
    }

    /** Empty constructor */
    constructor() : super(0) {
        name = "Unknown"
        imageName = ""
    }
}