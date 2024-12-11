package com.example.fitnessapp.panels

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.WorkoutRecyclerAdapter
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.StateEngine

/** Main Panel class to implement the logic in the main panel, where workouts are displayed */
class MainPanel: PanelFragment() {
    override var id: Long = Constants.PanelUniqueId.MAIN.ordinal.toLong()
    override var layoutId: Int = R.layout.main_panel
    override var panelIndex: Int = 0
    override var titleId: Int = R.string.main_panel_title

    private lateinit var titleLbl: TextView
    private lateinit var noWorkoutsLbl: TextView
    private lateinit var workoutsRecycler: RecyclerView
    private lateinit var newWorkoutBtn: Button

    override fun onResume() {
        super.onResume()

        if (StateEngine.refreshWorkouts) {
            populatePanel()
        }
    }

    override fun findViews() {
        titleLbl = panel.findViewById(R.id.latest_workouts_lbl)
        noWorkoutsLbl = panel.findViewById(R.id.no_workouts_lbl)
        workoutsRecycler = panel.findViewById(R.id.workouts_recycler)
        newWorkoutBtn = panel.findViewById(R.id.new_workout_btn)
    }

    override fun populatePanel() {
        WorkoutRepository().getWorkouts(onSuccess = { returnData ->
            val workouts = returnData.map { WorkoutModel(it) }.toMutableList()

            if (workouts.isEmpty()) {
                // Hide/display the views to show that there are no workouts
                titleLbl.visibility = View.GONE
                workoutsRecycler.visibility = View.GONE

                noWorkoutsLbl.visibility = View.VISIBLE
            } else {
                // Hide/display the views to show to workouts
                noWorkoutsLbl.visibility = View.GONE

                titleLbl.visibility = View.VISIBLE
                workoutsRecycler.visibility = View.VISIBLE

                workoutsRecycler.layoutManager = LinearLayoutManager(context)
                workoutsRecycler.adapter = WorkoutRecyclerAdapter(workouts) { workout ->
                    StateEngine.panelAdapter.displayWorkoutPanel(workout, null)
                }
            }

            // The most recent data with workouts is now displayed
            StateEngine.refreshWorkouts = false
        })
    }

    override fun addClickListeners() {
        newWorkoutBtn.setOnClickListener { AddEditWorkoutDialog(requireContext(), AddEditWorkoutDialog.Mode.ADD).show() }
    }
}