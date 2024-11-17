package com.example.fitnessapp.panels

import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.WorkoutRecyclerAdapter
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.StateEngine

/** Class to hold the logic for the Main Panel */
class MainPanel: PanelFragment() {
    override var id: Long = Constants.PanelUniqueId.MAIN.ordinal.toLong()
    override var layoutId: Int = R.layout.main_panel
    override var panelIndex: Int = 0
    override var titleId: Int = R.string.main_panel_title

    private lateinit var workoutsRecycler: RecyclerView
    private lateinit var newWorkoutBtn: Button

    override fun initializePanel() {
        // Find the views in the panel
        workoutsRecycler = panel.findViewById(R.id.workouts_recycler)
        newWorkoutBtn = panel.findViewById(R.id.new_workout_btn)

        // Add click button click listener
        newWorkoutBtn.setOnClickListener { AddEditWorkoutDialog(AddEditWorkoutDialog.Mode.ADD).showDialog() }

        // Populate the panel
        populatePanel()
    }

    override fun onResume() {
        super.onResume()

        if (StateEngine.refreshWorkouts) {
            populatePanel()
        }
    }

    /** Populates the data in the panel with the latest workouts */
    private fun populatePanel() {
        WorkoutRepository().getWorkouts(onSuccess = { returnData ->
            workoutsRecycler.layoutManager = LinearLayoutManager(context)
            workoutsRecycler.adapter = WorkoutRecyclerAdapter(
                returnData.map { WorkoutModel(it) }.toMutableList()) { workout ->
                StateEngine.panelAdapter.displayWorkoutPanel(workout, null)
            }

            // The most recent data with workouts is now displayed
            StateEngine.refreshWorkouts = false
        })
    }
}