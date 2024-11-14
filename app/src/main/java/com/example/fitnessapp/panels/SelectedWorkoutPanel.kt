package com.example.fitnessapp.panels

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.ExerciseRecyclerAdapter
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for the New Workout Panel */
class SelectedWorkoutPanel : PanelFragment(), FragmentRefreshListener {
    override var id: Long = 2
    override var layoutId: Int = R.layout.selected_workout_panel
    override var panelIndex: Int = 1
    override var titleId: Int = R.string.workout_panel_title

    private lateinit var currentWorkout: TextView
    private lateinit var currentWorkoutDate: TextView
    private lateinit var exerciseRecycler: RecyclerView
    private lateinit var newExerciseBtn: Button
    private lateinit var editBtn: Button

    override fun initializePanel() {
        // Find the views
        currentWorkout = panel.findViewById(R.id.current_workout_label)
        currentWorkoutDate = panel.findViewById(R.id.current_workout_date_label)
        exerciseRecycler = panel.findViewById(R.id.exercises_recycler)
        newExerciseBtn = panel.findViewById(R.id.add_exercise_btn)
        editBtn = panel.findViewById(R.id.edit_btn)

        // Set the click listeners
        newExerciseBtn.setOnClickListener { showAddExerciseDialog() }
        editBtn.setOnClickListener{ editWorkout() }

        // Set buttons visibility
        setButtonsVisibility()

        // Populate the panel
        populatePanel()
    }

    override fun onResume() {
        super.onResume()
        populatePanel()
    }

    /** Populates the data in the panel with the current workout */
    private fun populatePanel() {
        if (StateEngine.workout != null) {
            currentWorkout.text = StateEngine.workout!!.name
            currentWorkoutDate.text = Utils.defaultFormatDate(StateEngine.workout!!.date)

        } else {
            currentWorkout.text = requireContext().getString(R.string.current_workout_not_selected_lbl)
            currentWorkoutDate.text = ""

            if (exerciseRecycler.adapter != null) {
                getExercisesRecyclerAdapter().updateData(listOf())
            }
        }

        if (exerciseRecycler.adapter == null) {
            exerciseRecycler.layoutManager = LinearLayoutManager(context)
            exerciseRecycler.adapter = ExerciseRecyclerAdapter(listOf())
        }

        if (StateEngine.workout != null) {
            getExercisesRecyclerAdapter().updateData(StateEngine.workout!!.exercises)
        }

        // Set buttons visibility
        setButtonsVisibility()
    }

    /** Shows Exercise panel */
    private fun showAddExerciseDialog() {
        StateEngine.panelAdapter.displayTemporaryPanel(ExercisePanel())
    }

    /** Return the exercises recycler adapter */
    private fun getExercisesRecyclerAdapter(): ExerciseRecyclerAdapter {
        return exerciseRecycler.adapter as ExerciseRecyclerAdapter
    }

    /** Executed on Edit button click to open Edit Workout Dialog */
    private fun editWorkout() {
        AddEditWorkoutDialog(AddEditWorkoutDialog.Mode.EDIT).showDialog()
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

    /** Re-populates the panel, used when the viewPager current item index does not change,
     * but wee need to re-populate the panel */
    override fun onRefreshListener() {
        populatePanel()
    }
}