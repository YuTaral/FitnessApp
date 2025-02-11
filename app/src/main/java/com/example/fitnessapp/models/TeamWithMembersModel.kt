package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** TeamWithMembersModel class representing a team and it's members.
 *  Must correspond with server-side TeamWithMembersModel
 */
class TeamWithMembersModel: TeamModel {
    @SerializedName("Members")
    var members: List<TeamMemberModel>

    /** Constructor to accept serialized object
     * @param data serialized SetModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: TeamWithMembersModel = gson.fromJson(data, TeamWithMembersModel::class.java)

        image = model.image
        name = model.name
        description = model.description
        selectedInPanel = model.selectedInPanel
        viewTeamAs = model.viewTeamAs
        members = model.members
    }
}