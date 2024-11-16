package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** ExerciseRepository class, used to execute all requests related to exercise */
class ExerciseRepository {

    /** Adds new exercise to the given workout
     * @param exercise the exercise data
     * @param onSuccess callback to execute if request is successful
     */
    fun addExerciseToWorkout(exercise: ExerciseModel, onSuccess: (WorkoutModel) -> Unit) {
        // Send a request to add the workout
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to StateEngine.workout!!.id.toString())

        NetworkManager.sendRequest(
            APIService.instance.addExerciseToWorkout(params),
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.returnData[0])) }
        )
    }

    /** Edits the exercise from the given workout
     * @param exercise the exercise data
     * @param onSuccess callback to execute if request is successful
     */
    fun editExerciseFromWorkout(exercise: ExerciseModel, onSuccess: (WorkoutModel) -> Unit) {
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to StateEngine.workout!!.id.toString())

        NetworkManager.sendRequest(
            APIService.instance.updateExerciseFromWorkout(params),
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.returnData[0])) }
        )
    }

    /** Deletes the exercise from the workout
     * @param exerciseId the exercise id
     * @param onSuccess callback to execute if request is successful
     */
    fun deleteExerciseFromWorkout(exerciseId: Long, onSuccess: (WorkoutModel) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.deleteExerciseFromWorkout(exerciseId),
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.returnData[0])) }
        )
    }

    /** Fetches all exercises for this muscle group
     * @param muscleGroupId the muscle group id
     * @param onlyForUser "Y" if we need only user defined muscle group exercises, "N" if we need
     * user defined and the default ones
     * @param onSuccess callback to execute if request is successful
     */
    fun getMuscleGroupExercises(muscleGroupId: Long, onlyForUser: String,onSuccess:(MutableList<MGExerciseModel>) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.getExerciseByMGId(muscleGroupId, onlyForUser),
            onSuccessCallback = { response -> onSuccess(response.returnData.map { MGExerciseModel(it) }.toMutableList()) }
        )
    }

    /** Adds new exercise
     * @param exercise the exercise data
     * @param workoutId greater than 0 to add the newly created exercise to the current workout, 0 otherwise
     * @param onlyForUser used when workoutId is 0. "Y" if we need only user defined muscle group exercises
     * to be returned to the client, "N" if we need user defined and the default ones
     * @param onSuccess callback to execute if request is successful
     */
    fun addExercise(exercise: MGExerciseModel, workoutId: String, onlyForUser: String, onSuccess: (List<String>) -> Unit) {
        val params = mapOf("exercise" to Utils.serializeObject(exercise),
                    "workoutId" to workoutId, "onlyForUser" to onlyForUser)

        NetworkManager.sendRequest(
            APIService.instance.addExercise(params),
            onSuccessCallback = { response -> onSuccess(response.returnData) }
        )
    }

    /** Updates the exercise
     * @param exercise the exercise data
     * @param onSuccess callback to execute if request is successful
     */
    fun updateExercise(exercise: MGExerciseModel, onSuccess: (List<MGExerciseModel>) -> Unit) {
        val params = mapOf("exercise" to Utils.serializeObject(exercise))

        NetworkManager.sendRequest(
            APIService.instance.updateExercise(params),
            onSuccessCallback = { response ->
                onSuccess(response.returnData.map { MGExerciseModel(it)})
            }
        )
    }

    /** Deletes the exercise
     * @param mGExerciseId the muscle group exercise id
     * @param onSuccess callback to execute if request is successful
     */
    fun deleteExercise(mGExerciseId: Long, onSuccess: (List<MGExerciseModel>) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.deleteExercise(mGExerciseId),
            onSuccessCallback = { response ->
                onSuccess(response.returnData.map { MGExerciseModel(it) })
            }
        )
    }
}