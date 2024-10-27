package com.example.fitnessapp.panels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.WorkoutRecyclerAdapter
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.network.repositories.WorkoutTemplateRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils


/** Class to hold the logic for the Main Panel */
class MainPanel : Fragment() {
    private lateinit var panel: View
    private lateinit var workoutsRecycler: RecyclerView
    private lateinit var newWorkoutBtn: Button
    private lateinit var templateBtn: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        panel = inflater.inflate(R.layout.main_panel, container, false)

        // Find the views in the panel
        workoutsRecycler = panel.findViewById(R.id.workouts_recycler)
        newWorkoutBtn = panel.findViewById(R.id.new_workout_btn)
        templateBtn = panel.findViewById(R.id.new_workout_from_template_btn)

        // Add click button click listener
        newWorkoutBtn.setOnClickListener { startNewWorkout() }
        templateBtn.setOnClickListener { showWorkoutTemplate() }

        // Populate the panel
        populatePanel()

        return panel
    }

    override fun onResume() {
        super.onResume()

        if (StateEngine.refreshWorkouts) {
            populatePanel()
        }
    }

    /** Populates the data in the panel with the latest workouts */
    private fun populatePanel() {
        NetworkManager.sendRequest(
            APIService.instance.getWorkouts(),
            onSuccessCallback = { response ->
                workoutsRecycler.layoutManager = LinearLayoutManager(Utils.getContext())

                val workouts : MutableList<WorkoutModel> = mutableListOf()
                for (w : String in response.returnData) {
                    workouts.add(WorkoutModel(w))
                }

                workoutsRecycler.adapter = WorkoutRecyclerAdapter(workouts) { workout ->
                    NetworkManager.sendRequest(
                        APIService.instance.getWorkout(workout.id),
                        onSuccessCallback = { response ->
                            Utils.getActivity().displayWorkoutPanel(WorkoutModel(response.returnData[0]), null)
                        }
                    )
                }

                // The most recent data with workouts is now displayed
                StateEngine.refreshWorkouts = false
            }
        )
    }

    /** Executed on New Workout button click to open Add Workout Dialog */
    private fun startNewWorkout() {
        AddEditWorkoutDialog(true).showDialog()
    }

    /** Executed on Templates button click to display the workout templates */
    private fun showWorkoutTemplate() {
        WorkoutTemplateRepository().getWorkoutTemplates( onSuccess = { serializedTemplates ->
            val templates: MutableList<WorkoutModel> = mutableListOf()
            for (s: String in  serializedTemplates) {
                templates.add(WorkoutModel(s))
            }

            // TODO: Display the templates
        })
    }
}