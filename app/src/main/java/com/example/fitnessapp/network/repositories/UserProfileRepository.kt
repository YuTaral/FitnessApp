package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.UserDefaultValuesModel
import com.example.fitnessapp.models.UserModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.CustomResponse
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.Utils

/** UserProfileRepository class, used to execute all requests related to User Profile */
class UserProfileRepository {

    /** Change user default exercise values
     * @param values the data
     * @param onSuccess callback to execute if request is successful
     */
    fun updateUserDefaultValues(values: UserDefaultValuesModel, onSuccess: (CustomResponse) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.instance.updateUserDefaultValues(mapOf("values" to Utils.serializeObject(values))) },
            onSuccessCallback = { response -> onSuccess(response) }
        )
    }

    /** Send a request to fetch the default values for the exercise. If there are no default values
     * for the specific exercise, the request return the user default values.
     * @param mgExerciseId the muscle group exercise id
     * @param onSuccess callback to execute if request is successful
     */
    fun getUserDefaultValues(mgExerciseId: Long, onSuccess: (UserDefaultValuesModel) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.instance.getUserDefaultValues(mgExerciseId) },
            onSuccessCallback = { response -> onSuccess(UserDefaultValuesModel(response.data[0]))}
        )
    }

    /** Update the user profile
     * @param user the user
     * @param onSuccess callback to execute if request is successful
     */
    fun updateUserProfile(user: UserModel, onSuccess: (UserModel) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.instance.updateUserProfile(mapOf("user" to Utils.serializeObject(user))) },
            onSuccessCallback = { response -> onSuccess(UserModel(response.data[0]))}
        )
    }
}