package com.example.fitnessapp.panels

import android.widget.Button
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.CustomSpinnerAdapter
import com.example.fitnessapp.adapters.WorkoutRecyclerAdapter
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.dialogs.DialogAskQuestion
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutTemplateRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for Templates Panel */
class TemplatesPanel: PanelFragment() {
    override var id: Long = 3
    override var layoutId: Int = R.layout.templates_panel
    override var panelIndex: Int = 2
    override var titleId: Int = R.string.templates_lbl

    private lateinit var actionSpinner: Spinner
    private lateinit var templatesRecycler: RecyclerView
    private lateinit var closeBtn: Button
    private val startWorkoutIndex = 1

    override fun initializePanel() {
        // Find the views
        actionSpinner = panel.findViewById(R.id.template_list_action_spinner)
        templatesRecycler = panel.findViewById(R.id.templates_recycler)
        closeBtn = panel.findViewById(R.id.cancel_btn)

        // Populate the panel
        populatePanel()

        // Click listeners
        closeBtn.setOnClickListener { StateEngine.panelAdapter.displayWorkoutPanel() }
    }

    /** Populates the data in the panel */
    private fun populatePanel() {
        // Create the adapter
        actionSpinner.adapter = CustomSpinnerAdapter(panel.context, true, listOf(
            Utils.getContext().getString(R.string.select_action_lbl),
            Utils.getContext().getString(R.string.action_start_workout_from_template),
            Utils.getContext().getString(R.string.action_delete_template))
        )

        // Pre-select 'Start workout'
        actionSpinner.setSelection(1)

        // Get the templates and populate the recycler
        WorkoutTemplateRepository().getWorkoutTemplates(onSuccess = { serializedTemplates ->
            val templates: MutableList<WorkoutModel> = serializedTemplates.map { WorkoutModel(it) }.toMutableList()

            templatesRecycler.layoutManager = LinearLayoutManager(Utils.getContext())
            templatesRecycler.adapter = WorkoutRecyclerAdapter(templates, onClick = { template ->

                // Validate there is selected action
                if (actionSpinner.selectedItemPosition == 0) {
                    Utils.showToast(R.string.error_select_action)
                    return@WorkoutRecyclerAdapter
                }

                if (actionSpinner.selectedItemPosition == startWorkoutIndex) {
                    // Start workout is at index 1
                    AddEditWorkoutDialog(AddEditWorkoutDialog.Mode.ADD, template).showDialog()

                } else  {
                    // Delete the question otherwise
                    DialogAskQuestion(DialogAskQuestion.Question.DELETE_TEMPLATE).showDialog(template)

                }

            })
        }, onError = {
            StateEngine.panelAdapter.displayWorkoutPanel()
        })
    }
}