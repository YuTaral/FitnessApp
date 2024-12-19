package com.example.fitnessapp.panels

import android.widget.Button
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.CustomSpinnerAdapter
import com.example.fitnessapp.adapters.WorkoutRecyclerAdapter
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutTemplateRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Templates Panel class to implement the logic for managing templates */
class TemplatesPanel: BasePanel() {
    override var id: Long = Constants.PanelUniqueId.TEMPLATES.ordinal.toLong()
    override var layoutId: Int = R.layout.templates_panel
    override var panelIndex: Int = Constants.PanelIndices.TEMPORARY.ordinal
    override var titleId: Int = R.string.manage_templates_lbl

    private lateinit var actionSpinner: Spinner
    private lateinit var templatesRecycler: RecyclerView
    private lateinit var closeBtn: Button
    private val startWorkoutIndex = 0

    override fun findViews() {
        actionSpinner = panel.findViewById(R.id.template_list_action_spinner)
        templatesRecycler = panel.findViewById(R.id.templates_recycler)
        closeBtn = panel.findViewById(R.id.cancel_btn)
    }

    override fun populatePanel() {
        val ctx = requireContext()

        // Create the adapter
        actionSpinner.adapter = CustomSpinnerAdapter(panel.context, false, listOf(
            ctx.getString(R.string.action_start_workout_from_template),
            ctx.getString(R.string.action_delete_template))
        )

        // Pre-select 'Start workout'
        actionSpinner.setSelection(startWorkoutIndex)

        // Get the templates and populate the recycler
        WorkoutTemplateRepository().getWorkoutTemplates(onSuccess = { serializedTemplates ->
            val templates: MutableList<WorkoutModel> = serializedTemplates.map { WorkoutModel(it) }.toMutableList()

            templatesRecycler.layoutManager = LinearLayoutManager(context)
            templatesRecycler.adapter = WorkoutRecyclerAdapter(templates, onClick = { template ->

                if (actionSpinner.selectedItemPosition == startWorkoutIndex) {
                    // Start workout is at index 1
                    AddEditWorkoutDialog(requireContext(), AddEditWorkoutDialog.Mode.ADD, template).show()

                } else  {
                    // Delete the question otherwise
                    val dialog = AskQuestionDialog(requireContext(), AskQuestionDialog.Question.DELETE_TEMPLATE, template)

                    dialog.setLeftButtonCallback(callback = {
                        WorkoutTemplateRepository().deleteWorkoutTemplate(template.id, onSuccess = {
                            (templatesRecycler.adapter as WorkoutRecyclerAdapter).removeTemplate(template)
                            dialog.dismiss()
                        })
                    })

                    dialog.show()
                }
            })
        }, onError = {
            Utils.getPanelAdapter().displayWorkoutPanel(null)
        })
    }

    override fun addClickListeners() {
        closeBtn.setOnClickListener { Utils.getPanelAdapter().displayWorkoutPanel(null) }
    }
}