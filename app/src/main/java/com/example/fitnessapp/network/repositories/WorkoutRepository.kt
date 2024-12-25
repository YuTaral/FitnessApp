package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.WeightUnitModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.Utils

/** WorkoutRepository class, used to execute all requests related to workouts */
class WorkoutRepository {

    /** Add new workout
     * @param workout the workout data
     * @param onSuccess callback to execute if request is successful
     */
    fun addWorkout(workout: WorkoutModel, onSuccess: (WorkoutModel) -> Unit) {
        // Send a request to add the workout
        val params = mapOf("workout" to Utils.serializeObject(workout))

        NetworkManager.sendRequest(
            request = { APIService.getInstance().addWorkout(params) },
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.data[0])) }
        )
    }

    /** Edit the workout
     * @param workout the workout data
     * @param onSuccess callback to execute if request is successful
     */
    fun updateWorkout(workout: WorkoutModel, onSuccess: (WorkoutModel) -> Unit) {
        // Send a request to add the workout
        val params = mapOf("workout" to Utils.serializeObject(workout))

        NetworkManager.sendRequest(
            request = { APIService.getInstance().updateWorkout(params) },
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.data[0])) }
        )
    }

    /** Delete the workout
     * @param workoutId the workout id
     * @param onSuccess callback to execute if request is successful
     */
    fun deleteWorkout(workoutId: Long, onSuccess: () -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().deleteWorkout(workoutId) },
            onSuccessCallback = { onSuccess() })
    }

    /** Fetch the workouts
     * @param filterBy the filter value - ALL, IN_PROGRESS or COMPLETED
     * @param onSuccess callback to execute if request is successful
     */
    fun getWorkouts(filterBy: String, onSuccess: (List<String>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getWorkouts(filterBy) },
            onSuccessCallback = { response ->
                onSuccess(response.data)
            }
        )
    }

    /** Fetch the workout
     * @param workoutId the workout id
     * @param onSuccess callback to execute if request is successful
     */
    fun getWorkout(workoutId: Long, onSuccess: (WorkoutModel) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getWorkout(workoutId) },
            onSuccessCallback = { response ->
                onSuccess(WorkoutModel(response.data[0]))
            }
        )
    }

    /** Send a request to fetch the weight units
     * @param onSuccess callback to execute if request is successful
     */
    fun getWeightUnits(onSuccess: (List<WeightUnitModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getWeightUnits() },
            onSuccessCallback = { response -> onSuccess(response.data.map{ WeightUnitModel(it) })}
        )
    }
}