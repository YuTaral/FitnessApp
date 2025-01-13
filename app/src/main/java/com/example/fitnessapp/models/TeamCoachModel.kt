package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/**  TeamCoachModel class representing team coach.
 *  Must correspond with server-side TeamCoachModel class
 */
class TeamCoachModel: BaseModel {
    @SerializedName("FullName")
    var fullName: String

    @SerializedName("Image")
    var image: String

    /** Constructor to accept serialized object
     * @param data serialized SetModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: TeamCoachModel = gson.fromJson(data, TeamCoachModel::class.java)

        fullName = model.fullName
        image = model.image

    }
}