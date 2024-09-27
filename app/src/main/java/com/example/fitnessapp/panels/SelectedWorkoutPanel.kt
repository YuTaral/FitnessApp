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
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.dialogs.AddExerciseDialog
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for the New Workout Panel */
class SelectedWorkoutPanel(root: ViewGroup) {
    private val panel: View
    private val currentWorkout: TextView
    private val currentWorkoutDate: TextView
    private val currentMuscleGroups: TextView
    private val exerciseRecycler: RecyclerView
    private val newExerciseBtn: Button
    private val editBtn: Button

    init {
        // Inflate the panel
        panel = LayoutInflater.from(Utils.getContext()).inflate(R.layout.selected_workout_panel, root, false)

        // Find the views
        currentWorkout = panel.findViewById(R.id.current_workout_label)
        currentWorkoutDate = panel.findViewById(R.id.current_workout_date_label)
        currentMuscleGroups = panel.findViewById(R.id.muscle_groups_txt)
        exerciseRecycler = panel.findViewById(R.id.exercises_recycler)
        newExerciseBtn = panel.findViewById(R.id.add_exercise_btn)
        editBtn = panel.findViewById(R.id.edit_btn)

        // Set the click listeners
        newExerciseBtn.setOnClickListener { showAddExerciseDialog() }
        editBtn.setOnClickListener{ editWorkout() }

        // Set buttons visibility
        setButtonsVisibility()

        // Add the panel to the root view
        root.addView(panel)
    }

    /** Populates the data in the panel with the current workout */
    fun populatePanel() {
        var muscleGroupsSummary = ""

        if (StateEngine.workout != null) {
            currentWorkout.text = StateEngine.workout!!.name
            currentWorkoutDate.text = Utils.defaultFormatDate(StateEngine.workout!!.date)

            for (mg: MuscleGroupModel in StateEngine.workout!!.muscleGroups) {
                if (mg.checked) {
                    muscleGroupsSummary += mg.name + ", "
                }
            }

            if (muscleGroupsSummary.length > 2) {
                currentMuscleGroups.text = muscleGroupsSummary.dropLast(2)
            } else {
                currentMuscleGroups.text = ""
            }

        } else {
            currentWorkout.text = Utils.getContext().getString(R.string.current_workout_not_selected_lbl)
            currentWorkoutDate.text = ""
            currentMuscleGroups.text = ""

            if (exerciseRecycler.adapter != null) {
                getExercisesRecyclerAdapter().updateData(listOf())
            }
        }

        if (exerciseRecycler.adapter == null) {
            exerciseRecycler.layoutManager = LinearLayoutManager(Utils.getContext())
            exerciseRecycler.adapter = ExerciseRecyclerAdapter(listOf())
        }

        if (StateEngine.workout != null) {
            getExercisesRecyclerAdapter().updateData(StateEngine.workout!!.exercises)
        }

        // Set buttons visibility
        setButtonsVisibility()
    }

    /** Show Add Exercise dialog */
    private fun showAddExerciseDialog() {
        AddExerciseDialog().showDialog()
    }

    /** Return the exercises recycler adapter */
    private fun getExercisesRecyclerAdapter(): ExerciseRecyclerAdapter {
        return exerciseRecycler.adapter as ExerciseRecyclerAdapter
    }

    /** Executed on Edit button click to open Edit Workout Dialog */
    private fun editWorkout() {
        AddEditWorkoutDialog(false).showDialog()
    }

    /** Sets buttons visibility based on whether workout is selected */
    private fun setButtonsVisibility() {
        if (StateEngine.workout == null) {
            newExerciseBtn.visibility = View.GONE
            editBtn.visibility = View.GONE
        } else {
            newExerciseBtn.visibility = View.VISIBLE
            editBtn.visibility = View.VISIBLE
        }
    }
}