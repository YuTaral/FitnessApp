package com.example.fitnessapp.models

import com.example.fitnessapp.utils.Constants
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** TeamModel class representing a team.
 *  Must correspond with server-side TeamModel, excluding
 *  SelectedInPanel property
 */
class TeamModel: BaseModel {

    @SerializedName("Image")
    var image: String

    @SerializedName("Name")
    var name: String

    @SerializedName("Description")
    var description: String

    @SerializedName("PrivateNote")
    var privateNote: String

    @SerializedName("ViewTeamAs")
    var viewTeamAs: String

    var selectedInPanel: Boolean

    /** Constructor to accept serialized object
     * @param data serialized SetModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: TeamModel = gson.fromJson(data, TeamModel::class.java)

        image = model.image
        name = model.name
        description = model.description
        privateNote = model.privateNote
        selectedInPanel = model.selectedInPanel
        viewTeamAs = model.viewTeamAs
    }

    /** Constructor used when new TeamModel object is created */
    constructor(idVal: Long, imageVal: String, nameVal: String, descriptionVal: String, privateNoteVal: String) : super(idVal) {
        image = imageVal
        name = nameVal
        description = descriptionVal
        privateNote = privateNoteVal
        viewTeamAs = Constants.ViewTeamAs.COACH.toString()
        selectedInPanel = false
    }
}