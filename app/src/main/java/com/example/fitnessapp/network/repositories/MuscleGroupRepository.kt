package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager

/** MuscleGroupRepository class, used to execute all requests related to muscle groups */
class MuscleGroupRepository {

    /** Fetches muscle groups
     * @param userId the logged in user id
     * @param onSuccess callback to execute if request is successful
     */
    fun getMuscleGroups(userId: String, onSuccess: (MutableList<MuscleGroupModel>) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.getMuscleGroups(userId),
            onSuccessCallback = { response ->
                onSuccess(response.returnData.map { MuscleGroupModel(it) }.toMutableList())
            }
        )
    }

}