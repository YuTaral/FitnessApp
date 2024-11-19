package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.Utils

/** WorkoutRepository class, used to execute all requests related to workouts */
class WorkoutRepository {

    /** Adds new workout
     * @param workout the workout data
     * @param onSuccess callback to execute if request is successful
     */
    fun addWorkout(workout: WorkoutModel, onSuccess: (WorkoutModel) -> Unit) {
        // Send a request to add the workout
        val params = mapOf("workout" to Utils.serializeObject(workout))

        NetworkManager.sendRequest(APIService.instance.addWorkout(params),
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.responseData[0])) }
        )
    }

    /** Edits the workout
     * @param workout the workout data
     * @param onSuccess callback to execute if request is successful
     */
    fun editWorkout(workout: WorkoutModel, onSuccess: (WorkoutModel) -> Unit) {
        // Send a request to add the workout
        val params = mapOf("workout" to Utils.serializeObject(workout))

        NetworkManager.sendRequest(APIService.instance.editWorkout(params),
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.responseData[0])) }
        )
    }

    /** Deletes the workout
     * @param workoutId the workout id
     * @param onSuccess callback to execute if request is successful
     */
    fun deleteWorkout(workoutId: Long, onSuccess: () -> Unit) {
        NetworkManager.sendRequest(APIService.instance.deleteWorkout(workoutId),
            onSuccessCallback = { onSuccess() })
    }

    /** Fetches the workouts
     * @param onSuccess callback to execute if request is successful
     */
    fun getWorkouts(onSuccess: (List<String>) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.getWorkouts(),
            onSuccessCallback = { response ->
                onSuccess(response.responseData)
            }
        )
    }

    /** Fetches the workout
     * @param workoutId the workout id
     * @param onSuccess callback to execute if request is successful
     */
    fun getWorkout(workoutId: Long, onSuccess: (WorkoutModel) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.getWorkout(workoutId),
            onSuccessCallback = { response ->
                onSuccess(WorkoutModel(response.responseData[0]))
            }
        )
    }
}