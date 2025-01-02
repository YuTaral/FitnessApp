package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

/** JoinTeamNotificationModel class the show notification about team invitation.
 *  Must correspond with server-side JoinTeamNotificationModel
 */
class JoinTeamNotificationModel: BaseModel {

    @SerializedName("TeamName")
    val teamName: String

    @SerializedName("Description")
    val description: String

    @SerializedName("TeamImage")
    val teamImage: String

    @SerializedName("NotificationType")
    val notificationType: String

    @SerializedName("TeamId")
    val teamId: Long

    /** Constructor to deserialized NotificationDetailsModel object
     * @param data serialized NotificationDetailsModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: JoinTeamNotificationModel = gson.fromJson(data, JoinTeamNotificationModel::class.java)

        teamName = model.teamName
        description = model.description
        teamImage = model.teamImage
        notificationType = model.notificationType
        teamId = model.teamId
    }
}