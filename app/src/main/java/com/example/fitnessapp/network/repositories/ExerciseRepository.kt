package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.CustomResponse
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Utils

/** ExerciseRepository class, used to execute all requests related to exercise */
class ExerciseRepository {

    /** Add new exercise to the given workout
     * @param exercise the exercise data
     * @param onSuccess callback to execute if request is successful
     */
    fun addExerciseToWorkout(exercise: ExerciseModel, onSuccess: (WorkoutModel) -> Unit) {
        // Send a request to add the workout
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to AppStateManager.workout!!.id.toString())

        NetworkManager.sendRequest(
            request = { APIService.getInstance().addExerciseToWorkout(params) },
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.data[0])) }
        )
    }

    /** Edit the exercise from the given workout
     * @param exercise the exercise data
     * @param onSuccess callback to execute if request is successful
     */
    fun editExerciseFromWorkout(exercise: ExerciseModel, onSuccess: (WorkoutModel) -> Unit) {
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to AppStateManager.workout!!.id.toString())

        NetworkManager.sendRequest(
            request = { APIService.getInstance().updateExerciseFromWorkout(params) },
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.data[0])) }
        )
    }

    /** Delete the exercise from the workout
     * @param exerciseId the exercise id
     * @param onSuccess callback to execute if request is successful
     */
    fun deleteExerciseFromWorkout(exerciseId: Long, onSuccess: (WorkoutModel) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().deleteExerciseFromWorkout(exerciseId) },
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.data[0])) }
        )
    }

    /** Fetch all exercises for this muscle group
     * @param muscleGroupId the muscle group id
     * @param onlyForUser "Y" if we need only user defined muscle group exercises, "N" if we need
     * user defined and the default ones
     * @param onSuccess callback to execute if request is successful
     */
    fun getMuscleGroupExercises(muscleGroupId: Long, onlyForUser: String,onSuccess:(MutableList<MGExerciseModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getExerciseByMGId(muscleGroupId, onlyForUser) },
            onSuccessCallback = { response -> onSuccess(response.data.map { MGExerciseModel(it) }.toMutableList()) }
        )
    }

    /** Add new exercise
     * @param exercise the exercise data
     * @param workoutId greater than 0 to add the newly created exercise to the current workout, 0 otherwise
     * @param onlyForUser used when workoutId is 0. "Y" if we need only user defined muscle group exercises
     * to be returned to the client, "N" if we need user defined and the default ones
     * @param checkExistingEx "Y" when upon exercise creation check whether exercise with the same name
     * already exists must be executed, "N" otherwise
     * @param onSuccess callback to execute if request is successful
     * @param onFailure callback to execute if request failed
     */
    fun addExercise(exercise: MGExerciseModel, workoutId: String, onlyForUser: String, checkExistingEx: String,
                    onSuccess: (List<String>) -> Unit, onFailure: (CustomResponse) -> Unit) {

        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to workoutId,
                            "onlyForUser" to onlyForUser, "checkExistingEx" to checkExistingEx)

        NetworkManager.sendRequest(
            request = { APIService.getInstance().addExercise(params) },
            onSuccessCallback = { response -> onSuccess(response.data) },
            onErrorCallback = { response -> onFailure(response) }
        )
    }

    /** Update the exercise
     * @param exercise the exercise data
     * @param onlyForUser "Y" if the returned exercises should be only user defined, "N" if all
     * @param onSuccess callback to execute if request is successful
     */
    fun updateExercise(exercise: MGExerciseModel, onlyForUser: String, onSuccess: (List<String>) -> Unit) {
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "onlyForUser" to onlyForUser)

        NetworkManager.sendRequest(
            request = { APIService.getInstance().updateExercise(params) },
            onSuccessCallback = { response ->
                onSuccess(response.data)
            }
        )
    }

    /** Delete the exercise
     * @param mGExerciseId the muscle group exercise id
     * @param onSuccess callback to execute if request is successful
     */
    fun deleteExercise(mGExerciseId: Long, onSuccess: (List<MGExerciseModel>) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().deleteExercise(mGExerciseId) },
            onSuccessCallback = { response ->
                onSuccess(response.data.map { MGExerciseModel(it) })
            }
        )
    }

    /** Fetch specific muscle group exercise
     * @param mGExerciseId the muscle group exercise id
     * @param onSuccess callback to execute if request is successful
     */
    fun getMGExercise(mGExerciseId: Long, onSuccess:(MGExerciseModel) -> Unit) {
        NetworkManager.sendRequest(
            request = { APIService.getInstance().getMGExercise(mGExerciseId) },
            onSuccessCallback = { response -> onSuccess(MGExerciseModel(response.data[0])) }
        )
    }
}