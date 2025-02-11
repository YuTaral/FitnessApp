package com.example.fitnessapp.models

import com.example.fitnessapp.utils.Constants
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** TeamModel class representing a team.
 *  Must correspond with server-side TeamModel, excluding
 *  SelectedInPanel property
 */
open class TeamModel: BaseModel {

    @SerializedName("Image")
    var image: String

    @SerializedName("Name")
    var name: String

    @SerializedName("Description")
    var description: String

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
        selectedInPanel = model.selectedInPanel
        viewTeamAs = model.viewTeamAs
    }

    /** Constructor used when new TeamModel object is created */
    constructor(idVal: Long, imageVal: String, nameVal: String, descriptionVal: String) : super(idVal) {
        image = imageVal
        name = nameVal
        description = descriptionVal
        viewTeamAs = Constants.ViewTeamAs.COACH.toString()
        selectedInPanel = false
    }
}