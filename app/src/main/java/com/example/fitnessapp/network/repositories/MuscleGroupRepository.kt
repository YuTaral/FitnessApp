package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager

/** MuscleGroupRepository class, used to execute all requests related to muscle groups */
class MuscleGroupRepository {

    /** Fetch muscle groups
     * @param onSuccess callback to execute if request is successful
     */
    fun getMuscleGroups(onSuccess: (MutableList<MuscleGroupModel>) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.getMuscleGroups(),
            onSuccessCallback = { response ->
                onSuccess(response.data.map { MuscleGroupModel(it) }.toMutableList())
            }
        )
    }

}