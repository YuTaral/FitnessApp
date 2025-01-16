package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.Utils

/** WorkoutTemplateRepository class, used to execute all requests related to workout templates */
class WorkoutTemplateRepository {

    /** Add new workout template
     * @param workout the workout template data
     * @param onSuccess callback to execute if request is successful
     */
    fun addWorkoutTemplate(workout: WorkoutModel, onSuccess: () -> Unit) {
        val params = mapOf("workout" to Utils.serializeObject(workout))

        NetworkManager.sendRequest(
            request = { APIService.getInstance().addWorkoutTemplate(params) },
            onSuccessCallback = { onSuccess() }
        )
    }

    /** Fetch workout templates which has been added by the user
     * @param onSuccess callback to execute if request is successful
     * @param onError callback to execute if request failed
     */
    fun getWorkoutTemplates(onSuccess: (List<String>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getWorkoutTemplates() },
            onSuccessCallback = { response -> onSuccess(response.data) }
        )
    }

    /** Delete the workout template
     * @param id the template id
     * @param onSuccess callback to execute if request is successful
     */
    fun deleteWorkoutTemplate(id: Long, onSuccess: () -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().deleteWorkoutTemplate(id) },
            onSuccessCallback = { onSuccess() }
        )
    }
}