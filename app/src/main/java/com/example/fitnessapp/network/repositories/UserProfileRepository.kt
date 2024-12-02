package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.UserDefaultValuesModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.Utils

/** UserProfileRepository class, used to execute all requests related to User Profile */
class UserProfileRepository {
    /** Change user default exercise values
     * @param values the data
     * @param onSuccess callback to execute if request is successful
     */
    fun updateUserDefaultValues(values: UserDefaultValuesModel, onSuccess: (UserDefaultValuesModel) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.updateUserDefaultValues(mapOf("values" to Utils.serializeObject(values))),
            onSuccessCallback = { response -> onSuccess(UserDefaultValuesModel(response.responseData[0])) })
    }
}