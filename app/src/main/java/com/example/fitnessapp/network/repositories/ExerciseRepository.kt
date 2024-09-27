package com.example.fitnessapp.network.repositories

import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** ExerciseRepository class, used to execute all requests related to exercise */
class ExerciseRepository {

    /** Adds new exercise
     * @param exercise the exercise data
     * @param onSuccess callback to execute if request is successful
     */
    fun addExercise(exercise: ExerciseModel, onSuccess: (WorkoutModel) -> Unit) {
        // Send a request to add the workout
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to StateEngine.workout!!.id.toString())

        NetworkManager.sendRequest(
            APIService.instance.addExercise(params),
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.returnData[0])) }
        )
    }

    /** Edits the exercise
     * @param exercise the exercise data
     * @param onSuccess callback to execute if request is successful
     */
    fun editExercise(exercise: ExerciseModel, onSuccess: (WorkoutModel) -> Unit) {
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to StateEngine.workout!!.id.toString())

        NetworkManager.sendRequest(
            APIService.instance.updateExercise(params),
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.returnData[0])) }
        )
    }

    /** Deletes the exercise
     * @param exerciseId the exercise id
     * @param onSuccess callback to execute if request is successful
     */
    fun deleteExercise(exerciseId: Long,  onSuccess: (WorkoutModel) -> Unit) {
        NetworkManager.sendRequest(
            APIService.instance.deleteExercise(exerciseId),
            onSuccessCallback = { response -> onSuccess(WorkoutModel(response.returnData[0])) }
        )
    }
}