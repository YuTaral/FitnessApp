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
     * @param onSuccess callback to execute if request is successful
     */
    fun getMuscleGroupExercises(muscleGroupId: Long, onSuccess:(MutableList<MGExerciseModel>) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.getExerciseByMGId(muscleGroupId),
            onSuccessCallback = { response -> onSuccess(response.returnData.map { MGExerciseModel(it) }.toMutableList()) }
        )
    }

    /** Adds new exercise to the given workout
     * @param exercise the exercise data
     * @param workoutId greater than 0 to add the newly created exercise to the current workout,
     * 0 otherwise
     * @param onSuccess callback to execute if request is successful
     */
    fun addExercise(exercise: MGExerciseModel, workoutId: String, onSuccess: (List<String>) -> Unit) {
        // Send a request to add new exercise to the muscle group group
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to workoutId)

        NetworkManager.sendRequest(
            APIService.instance.addExercise(params),
            onSuccessCallback = { response -> onSuccess(response.returnData) }
        )
    }
}