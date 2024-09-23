package com.example.fitnessapp.panels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
class SelectedWorkoutPanel(root: ViewGroup) {
    private val panel: View
    private val currentWorkout: TextView
    private val currentWorkoutDate: TextView
    private val exerciseRecycler: RecyclerView
    private val newExerciseBtn: Button
    private val editBtn: Button

    init {
        // Inflate the panel
        panel = LayoutInflater.from(Utils.getContext()).inflate(R.layout.selected_workout_panel, root, false)

        // Find the views
        currentWorkout = panel.findViewById(R.id.current_workout_label)
        currentWorkoutDate = panel.findViewById(R.id.current_workout_date_label)
        exerciseRecycler = panel.findViewById(R.id.exercises_recycler)
        newExerciseBtn = panel.findViewById(R.id.add_exercise_btn)
        editBtn = panel.findViewById(R.id.edit_btn)

        // Set the click listeners
        newExerciseBtn.setOnClickListener { showAddExerciseDialog() }

        // Set buttons visibility
        if (StateEngine.workout == null) {
            newExerciseBtn.visibility = View.GONE
            editBtn.visibility = View.GONE
        } else {
            newExerciseBtn.visibility = View.VISIBLE
            editBtn.visibility = View.VISIBLE
        }

        // Add the panel to the root view
        root.addView(panel)
    }

    /** Populates the data in the panel with the current workout */
    fun populatePanel() {
        if (StateEngine.workout != null) {
            currentWorkout.text = StateEngine.workout!!.name
            currentWorkoutDate.text = Utils.defaultFormatDate(StateEngine.workout!!.date)
        }

        if (exerciseRecycler.adapter == null) {
            exerciseRecycler.layoutManager = LinearLayoutManager(Utils.getContext())
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
        // Send a request to add the exercise
        val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to StateEngine.workout!!.id.toString())

        NetworkManager.sendRequest(APIService.instance.addExercise(params),
            onSuccessCallback = { response ->
                StateEngine.workout = WorkoutModel(response.returnData.first())
                populatePanel()
                Utils.showToast(R.string.exercise_updated)
            })

        StateEngine.refreshWorkouts = true
    }

    /** Return the exercises recycler adapter */
    private fun getExercisesRecyclerAdapter(): ExerciseRecyclerAdapter {
        return exerciseRecycler.adapter as ExerciseRecyclerAdapter
    }

}