package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.JoinTeamNotificationModel
import com.example.fitnessapp.models.NotificationModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.Utils

/** NotificationRepository class, used to execute all requests related to notifications */
class NotificationRepository {

    /** Get the notifications for the logged in user
     * @param onSuccess callback to execute if request is successful
     */
    fun getNotifications(onSuccess: (List<NotificationModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getNotifications() },
            onSuccessCallback = { response -> onSuccess(response.data.map { NotificationModel(it) } )},
        )
    }

    /** Get the notification details
     * @param id the notification id
     * @param onSuccess callback to execute if request is successful
     */
    fun getNotificationDetails(id: Long, onSuccess: (JoinTeamNotificationModel) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getJoinTeamNotificationDetails(id) },
            onSuccessCallback = { response -> onSuccess(JoinTeamNotificationModel(response.data[0]))},
        )
    }

    /** Mark the notification as reviewed (inactive)
     * @param id the notification id
     * @param onSuccess callback to execute if request is successful
     */
    fun notificationReviewed(id: Long, onSuccess: () -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().notificationReviewed(id) },
            onSuccessCallback = { onSuccess() },
        )
    }

    /** Delete the notification
     * @param notification the notification to remove
     * @param onSuccess callback to execute if request is successful
     */
    fun deleteNotification(notification: NotificationModel, onSuccess: (List<NotificationModel>) -> Unit) {
        val params = mapOf("notification" to Utils.serializeObject(notification))

        NetworkManager.sendRequest(
            request = { APIService.getInstance().deleteNotification(params) },
            onSuccessCallback = { response -> onSuccess(response.data.map { NotificationModel(it) }) },
        )
    }

    /** Send refresh request to update notifications
     * @param onResponse callback to execute when response is received
     */
    fun refreshNotifications(onResponse: () -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().refreshNotifications() },
            onSuccessCallback = { onResponse() },
        )
    }
}