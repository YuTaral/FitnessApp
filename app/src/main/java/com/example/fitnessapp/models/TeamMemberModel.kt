package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** TeamMemberModel class representing team member.
 * Must correspond with server-side TeamMemberModel, excluding selectedForAssign
 * which is used only client side
 */
class TeamMemberModel(data: String) : BaseModel(data) {
    @SerializedName("UserId")
    var userId: String

    @SerializedName("TeamId")
    var teamId: Long

    @SerializedName("FullName")
    var fullName: String

    @SerializedName("Image")
    var image: String

    @SerializedName("TeamState")
    var teamState: String

    var selectedForAssign: Boolean

    init {
        val gson = Gson()
        val model: TeamMemberModel = gson.fromJson(data, TeamMemberModel::class.java)

        id = model.id
        teamId = model.teamId
        userId = model.userId
        fullName = model.fullName
        image = model.image
        teamState = model.teamState
        selectedForAssign = false
    }
}