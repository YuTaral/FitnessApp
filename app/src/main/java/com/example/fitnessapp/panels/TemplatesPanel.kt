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
import com.example.fitnessapp.dialogs.DialogAskQuestion
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutTemplateRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for Templates Panel */
class TemplatesPanel(m: Mode): Fragment() {
    /** The templates panel mode - start workout from template or delete template upon click */
    enum class Mode {
        START_WORKOUT,
        DELETE_TEMPLATE
    }

    private lateinit var panel: View
    private lateinit var templatesRecycler: RecyclerView
    private lateinit var closeBtn: Button
    private var panelMode = m

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        panel = inflater.inflate(R.layout.templates_panel, container, false)

        // Find the views
        templatesRecycler = panel.findViewById(R.id.templates_recycler)
        closeBtn = panel.findViewById(R.id.cancel_btn)

        // Populate the panel
        populatePanel(panelMode)

        // Click listeners
        closeBtn.setOnClickListener { StateEngine.panelAdapter.displayWorkoutPanel() }

        return panel
    }

    /** Populates the data in the panel
     * @param mode the panel mode
     * @param overrideMode whether panelMode property should be overridden, false by default
     * */
    fun populatePanel(mode: Mode, overrideMode: Boolean = false) {
        if (overrideMode) {
            panelMode = mode
        }

        WorkoutTemplateRepository().getWorkoutTemplates(onSuccess = { serializedTemplates ->
            val templates: MutableList<WorkoutModel> = serializedTemplates.map { WorkoutModel(it) }.toMutableList()

            templatesRecycler.layoutManager = LinearLayoutManager(Utils.getContext())
            templatesRecycler.adapter = WorkoutRecyclerAdapter(templates, onClick = { template ->

                if (panelMode == Mode.START_WORKOUT) {
                    AddEditWorkoutDialog(true, template).showDialog()
                } else if (panelMode == Mode.DELETE_TEMPLATE) {
                    DialogAskQuestion(DialogAskQuestion.Question.DELETE_TEMPLATE).showDialog(template)
                }

            })
        }, onError = {
            StateEngine.panelAdapter.displayWorkoutPanel()
        })
    }
}