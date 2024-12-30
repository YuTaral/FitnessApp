package com.example.fitnessapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.util.Date

/** NotificationModel class representing a notification.
 *  Must correspond with server-side NotificationModel
 */
class NotificationModel: BaseModel {

    @SerializedName("NotificationText")
    val notificationText: String

    @SerializedName("DateTime")
    val dateTime: Date

    @SerializedName("IsActive")
    val isActive: Boolean

    /** Constructor to deserialized NotificationModel object
     * @param data serialized NotificationModel object
     */
    constructor(data: String) : super(data) {
        val gson = Gson()
        val model: NotificationModel = gson.fromJson(data, NotificationModel::class.java)

        notificationText = model.notificationText
        dateTime = model.dateTime
        isActive = model.isActive
    }
}