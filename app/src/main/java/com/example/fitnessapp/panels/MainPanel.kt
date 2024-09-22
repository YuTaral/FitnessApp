package com.example.fitnessapp.panels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.WorkoutRecyclerAdapter
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils


/** Class to hold the logic for the Main Panel */
class MainPanel(root: ViewGroup) {
    private val panel: View
    private val workoutsRecycler: RecyclerView

    init {
        panel = LayoutInflater.from(Utils.getActivity()).inflate(R.layout.main_panel, root, false)

        // Find the views in the panel
        workoutsRecycler = panel.findViewById(R.id.workouts_recycler)

        populatePanel()

        // Add the panel to the root view
        root.addView(panel)
    }

    /** Populates the data in the panel with the latest workouts */
    private fun populatePanel() {
        if (workoutsRecycler.adapter == null) {
            NetworkManager.sendRequest(
                APIService.instance.getWorkouts(StateEngine.user.id),
                onSuccessCallback = { response ->
                    workoutsRecycler.layoutManager = LinearLayoutManager(Utils.getActivity())

                    val workouts : MutableList<WorkoutModel> = mutableListOf()
                    for (w : String in response.returnData) {
                        workouts.add(WorkoutModel(w))
                    }

                    workoutsRecycler.adapter = WorkoutRecyclerAdapter(workouts)
                }
            )
        }
    }

}