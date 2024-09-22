package com.example.fitnessapp.panels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.ExerciseRecyclerAdapter
import com.example.fitnessapp.dialogs.AddExerciseDialog
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for the New Workout Panel */
class NewWorkoutPanel(root: ViewGroup) {
    private val panel: View
    private val workoutName: EditText
    private val exerciseRecycler: RecyclerView
    private val newExerciseBtn: Button

    init {
        // Inflate the panel
        panel = LayoutInflater.from(Utils.getActivity()).inflate(R.layout.add_workout_panel, root, false)

        // Find the views
        workoutName = panel.findViewById(R.id.workout_name_txt)
        exerciseRecycler = panel.findViewById(R.id.exercises_recycler)
        newExerciseBtn = panel.findViewById(R.id.add_exercise_btn)

        // Populate the data
        populatePanel()

        // Set the click listeners
        newExerciseBtn.setOnClickListener {
            if (workoutName.text.isEmpty()) {
                Utils.showMessage(R.string.error_msg_enter_workout_name)
                Utils.openKeyboardOnInput(workoutName)
                return@setOnClickListener
            }

            showAddExerciseDialog()
        }

        // Add the panel to the root view
        root.addView(panel)
    }

    /** Populates the data in the panel with the current workout */
    private fun populatePanel() {
        if (StateEngine.workout != null) {
            workoutName.setText(StateEngine.workout!!.name)
        }

        if (exerciseRecycler.adapter == null) {
            exerciseRecycler.layoutManager = LinearLayoutManager(Utils.getActivity())
            exerciseRecycler.adapter = ExerciseRecyclerAdapter(listOf(), onSuccessChange = { populatePanel() })
        }

        if (StateEngine.workout != null) {
            getExercisesRecyclerAdapter().updateData(StateEngine.workout!!.exercises)
        }
    }

    /** Show Add Exercise dialog */
    private fun showAddExerciseDialog() {
        AddExerciseDialog.showDialog(onSave = { exercise -> run { addDialogOnSave(exercise) } })
    }

    /** Executes Add Exercise Dialog button Save clicked to send a request and create a new workout
     * or update the existing workout with the newly created exercise
     * @param exercise - the created exercise
     */
    private fun addDialogOnSave(exercise: ExerciseModel) {
        if (StateEngine.workout != null) {
            // Workout is already saved, send a request to add the exercise
            val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to StateEngine.workout!!.id.toString())

            NetworkManager.sendRequest(APIService.instance.addExercise(params),
                onSuccessCallback = { response ->
                    StateEngine.workout = WorkoutModel(response.returnData.first())
                    populatePanel()
                    Utils.showToast(R.string.exercise_updated)
                })

        } else {
            // Send a request to create the workout
            val workout = WorkoutModel(workoutName.text.toString(), mutableListOf(exercise))
            val params = mapOf("workout" to Utils.serializeObject(workout), "userId" to StateEngine.user.id)

            NetworkManager.sendRequest(APIService.instance.addWorkout(params),
                onSuccessCallback = { response ->
                    StateEngine.workout = WorkoutModel(response.returnData.first())
                    populatePanel()
                    Utils.showToast(R.string.workout_saved)
                })
        }
    }

    /** Return the exercises recycler adapter */
    private fun getExercisesRecyclerAdapter(): ExerciseRecyclerAdapter {
        return exerciseRecycler.adapter as ExerciseRecyclerAdapter
    }

}