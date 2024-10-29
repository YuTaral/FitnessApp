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
import com.example.fitnessapp.network.repositories.WorkoutTemplateRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for Templates Panel */
class TemplatesPanel: Fragment() {
    private lateinit var panel: View
    private lateinit var templatesRecycler: RecyclerView
    private lateinit var closeBtn: Button
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        panel = inflater.inflate(R.layout.templates_panel, container, false)

        // Find the views
        templatesRecycler = panel.findViewById(R.id.templates_recycler)
        closeBtn = panel.findViewById(R.id.cancel_btn)

        // Populate the panel
        populatePanel()

        // Click listeners
        closeBtn.setOnClickListener { StateEngine.panelAdapter.displayWorkoutPanel() }

        return panel
    }

    /** Populates the data in the panel */
    private fun populatePanel() {
        WorkoutTemplateRepository().getWorkoutTemplates( onSuccess = { serializedTemplates ->
            val templates: MutableList<WorkoutModel> = serializedTemplates.map { WorkoutModel(it) }.toMutableList()

            templatesRecycler.layoutManager = LinearLayoutManager(Utils.getContext())
            templatesRecycler.adapter = WorkoutRecyclerAdapter(templates, onClick = { workout ->
                confirmWorkoutName(workout)
            })
        })
    }

    /** Used to confirm Workout name when creating workout from Template */
    private fun confirmWorkoutName(template: WorkoutModel) {
        AddEditWorkoutDialog(true, template).showDialog()
    }
}