package com.example.fitnessapp.panels

import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.WorkoutRecyclerAdapter
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

    private lateinit var templatesRecycler: RecyclerView
    private lateinit var closeBtn: Button

    override fun initializePanel() {
        // Find the views
        templatesRecycler = panel.findViewById(R.id.templates_recycler)
        closeBtn = panel.findViewById(R.id.cancel_btn)

        // Populate the panel
        populatePanel()

        // Click listeners
        closeBtn.setOnClickListener { StateEngine.panelAdapter.displayWorkoutPanel() }
    }

    /** Populates the data in the panel */
    private fun populatePanel() {

        WorkoutTemplateRepository().getWorkoutTemplates(onSuccess = { serializedTemplates ->
            val templates: MutableList<WorkoutModel> = serializedTemplates.map { WorkoutModel(it) }.toMutableList()

            templatesRecycler.layoutManager = LinearLayoutManager(Utils.getContext())
            templatesRecycler.adapter = WorkoutRecyclerAdapter(templates, onClick = { template ->

//                if (panelMode == Constants.TemporaryPanelMode.TEMPLATES_START_WORKOUT) {
//                    AddEditWorkoutDialog(AddEditWorkoutDialog.Mode.ADD, template).showDialog()
//
//                } else if (panelMode == Constants.TemporaryPanelMode.TEMPLATES_DELETE) {
//                    DialogAskQuestion(DialogAskQuestion.Question.DELETE_TEMPLATE).showDialog(template)
//
//                }

            })
        }, onError = {
            StateEngine.panelAdapter.displayWorkoutPanel()
        })
    }
}