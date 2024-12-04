package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.CustomResponse
import com.example.fitnessapp.network.NetworkManager

/** UserRepository class, used to execute all requests related to User */
class UserRepository {

    /** Register the user with the given email and password
     * @param email the email
     * @param password the password
     * @param onSuccess callback to execute if request is successful
     */
    fun register(email: String, password: String, onSuccess: () -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.register(mapOf("email" to email, "password" to password)),
            onSuccessCallback = { onSuccess() })
    }

    /** Login the user with the given email and password
     * @param email the email
     * @param password the password
     * @param onSuccess callback to execute if request is successful
     */
    fun login(email: String, password: String, onSuccess: (CustomResponse) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.login(mapOf("email" to email, "password" to password)),
            onSuccessCallback = { response ->
                onSuccess(response)
            })
    }

    /** Logout the user
     * @param onSuccess callback to execute if request is successful
     */
    fun logout(onSuccess: () -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.logout(),
            onSuccessCallback = {
                onSuccess()
            })
    }

    /** Change user password
     * @param oldPassword the old password
     * @param password the new password
     * @param onSuccess callback to execute if request is successful
     */
    fun changePassword(oldPassword: String, password: String, onSuccess: () -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.changePassword(mapOf("oldPassword" to oldPassword, "password" to password)),
            onSuccessCallback = { onSuccess() })
    }

    /** Validate the token
     * @param token the token
     * @param onSuccess callback to execute if request is successful
     */
    fun validateToken(token: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.validateToken(mapOf("token" to token)),
            onSuccessCallback = { onSuccess() },
            onErrorCallback = { onFailure() }
        )
    }
}