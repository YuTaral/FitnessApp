package com.example.fitnessapp.network.repositories

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
}