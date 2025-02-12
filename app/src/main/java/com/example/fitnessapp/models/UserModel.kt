package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** UserModel class representing the logged in user.
 * Must correspond with server-side UserModel
 * Do not inherit BaseModel, userId is string in ASP .NET by default
 */
class UserModel {
    @SerializedName("Id")
    val id: String

    @SerializedName("Email")
    val email: String

    @SerializedName("FullName")
    var fullName: String

    @SerializedName("ProfileImage")
    var profileImage: String

    @SerializedName("DefaultValues")
    var defaultValues: UserDefaultValuesModel

    constructor(data: String) {
        val gson = Gson()
        val model: UserModel = gson.fromJson(data, UserModel::class.java)

        id = model.id
        email = model.email
        fullName = model.fullName
        profileImage = model.profileImage
        defaultValues = model.defaultValues
    }

    constructor(idVal: String, emailVal: String, fullNameVal: String, profileImageVal: String, defaultValuesVal: UserDefaultValuesModel) {
        id = idVal
        email = emailVal
        fullName = fullNameVal
        profileImage = profileImageVal
        defaultValues = defaultValuesVal
    }
}