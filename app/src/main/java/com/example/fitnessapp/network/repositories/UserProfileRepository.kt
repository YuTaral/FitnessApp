package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.UserDefaultValuesModel
import com.example.fitnessapp.models.WeightUnitModel
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
            APIService.instance.updateUserDefaultValues(mapOf("values" to Utils.serializeObject(values))),
            onSuccessCallback = { response -> onSuccess(response) })
    }

    /** Send a request to fetch the default values for the exercise. If there are no default values
     * for the specific exercise, the request return the user default values.
     * @param mgExerciseId the muscle group exercise id
     * @param onSuccess callback to execute if request is successful
     */
    fun getUserDefaultValues(mgExerciseId: Long, onSuccess: (UserDefaultValuesModel) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.getUserDefaultValues(mgExerciseId),
            onSuccessCallback = { response -> onSuccess(UserDefaultValuesModel(response.responseData[0]))}
        )
    }

    /** Send a request to fetch the weight units
     * @param onSuccess callback to execute if request is successful
     */
    fun getWeightUnits(onSuccess: (List<WeightUnitModel>) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.getWeightUnits(),
            onSuccessCallback = { response -> onSuccess(response.responseData.map{ WeightUnitModel(it) })}
        )
    }
}