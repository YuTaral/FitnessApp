package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** UserModel class representing the logged in user.
 * Must correspond with server-side UserModel
 * Do not inherit BaseModel, userId is string in ASP .NET by default
 */
class UserModel(data: String) {
    @SerializedName("Id")
    val id: String

    @SerializedName("Email")
    val email: String

    /** Init method, deserializes data into UserModel object */
    init {
        val gson = Gson()
        val model: UserModel = gson.fromJson(data, UserModel::class.java)

        id = model.id
        email = model.email
    }
}