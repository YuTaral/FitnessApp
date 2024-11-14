package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** UserModel class representing the logged in user.
 * Must correspond with server-side UserModel
 */
class UserModel(data: String) {
    @SerializedName("Id")
    val id: String

    @SerializedName("Email")
    val email: String

    init {
        val gson = Gson()
        val model: UserModel = gson.fromJson(data, UserModel::class.java)

        id = model.id
        email = model.email
    }
}