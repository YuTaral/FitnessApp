package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.JoinTeamNotificationModel
import com.example.fitnessapp.models.NotificationModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager

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
     * @param notificationId the notification id
     * @param onSuccess callback to execute if request is successful
     */
    fun getNotificationDetails(notificationId: Long, onSuccess: (JoinTeamNotificationModel) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getJoinTeamNotificationDetails(notificationId) },
            onSuccessCallback = { response -> onSuccess(JoinTeamNotificationModel(response.data[0]))},
        )
    }

    /** Mark the notification as reviewed (inactive)
     * @param id the notification id
     * @param onSuccess callback to execute if request is successful
     */
    fun notificationReviewed(id: Long, onSuccess: () -> Unit) {
        val params = mapOf("id" to id.toString())

        NetworkManager.sendRequest(
            request = { APIService.getInstance().notificationReviewed(params) },
            onSuccessCallback = { onSuccess() },
        )
    }

    /** Delete the notification
     * @param notificationId the notification id to remove
     * @param onSuccess callback to execute if request is successful
     */
    fun deleteNotification(notificationId: Long, onSuccess: (List<NotificationModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().deleteNotification(notificationId) },
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