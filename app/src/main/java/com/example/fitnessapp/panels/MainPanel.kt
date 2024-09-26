package com.example.fitnessapp.panels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.WorkoutRecyclerAdapter
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils


/** Class to hold the logic for the Main Panel */
class MainPanel(root: ViewGroup) {
    private val panel: View
    private val workoutsRecycler: RecyclerView
    private val newWorkoutBtn: Button

    init {
        panel = LayoutInflater.from(Utils.getContext()).inflate(R.layout.main_panel, root, false)

        // Find the views in the panel
        workoutsRecycler = panel.findViewById(R.id.workouts_recycler)
        newWorkoutBtn = panel.findViewById(R.id.new_workout_btn)

        populatePanel()

        // Add click button click listener
        newWorkoutBtn.setOnClickListener { startNewWorkout() }

        // Add the panel to the root view
        root.addView(panel)
    }

    /** Populates the data in the panel with the latest workouts */
     fun populatePanel() {
        NetworkManager.sendRequest(
            APIService.instance.getWorkouts(StateEngine.user.id),
            onSuccessCallback = { response ->
                workoutsRecycler.layoutManager = LinearLayoutManager(Utils.getContext())

                val workouts : MutableList<WorkoutModel> = mutableListOf()
                for (w : String in response.returnData) {
                    workouts.add(WorkoutModel(w))
                }

                workoutsRecycler.adapter = WorkoutRecyclerAdapter(workouts) { workout ->
                    Utils.getActivity().selectWorkout(workout)
                }

                // The most recent data with workouts is now displayed
                StateEngine.refreshWorkouts = false
            }
        )
    }

    /** Executed on New Workout button click to open Add Workout Dialog */
    private fun startNewWorkout() {
        AddEditWorkoutDialog.showDialog(true, onSave = { workout -> run {
            // Send a request to add the workout
            val params = mapOf("workout" to Utils.serializeObject(workout), "userId" to StateEngine.user.id)

            NetworkManager.sendRequest(APIService.instance.addWorkout(params),
                onSuccessCallback = { response ->
                    Utils.showToast(R.string.workout_added)
                    StateEngine.workout = WorkoutModel(response.returnData.first())
                    Utils.getActivity().displayNewWorkoutPanel()
                    StateEngine.refreshWorkouts = true
                }
            )
        }})
    }
}