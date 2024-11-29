package com.example.fitnessapp.panels

import android.view.View
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.CustomSpinnerAdapter
import com.example.fitnessapp.dialogs.AddEditMGExerciseDialog
import com.example.fitnessapp.dialogs.DialogAskQuestion
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for Manage Exercises Panel */
class ManageExercisesPanel(mode: Mode): BaseExercisePanel(mode) {
    override var id: Long = Constants.PanelUniqueId.MANAGE_EXERCISE.ordinal.toLong()
    override var layoutId: Int = R.layout.exercise_panel
    override var panelIndex: Int = 2
    override var titleId: Int = R.string.manage_exercises_lbl
    override var onlyForUser: String = "Y"
    override var noExercisesStringId: Int = R.string.no_exercise_to_edit_lbl

    private var actionSpinnerEditExIndex: Int = 1

    override fun additionalPanelInitialization() {
        actionSpinner = panel.findViewById(R.id.exercise_action_spinner)

        actionSpinner.adapter = CustomSpinnerAdapter(panel.context, true, listOf(
            requireContext().getString(R.string.select_action_lbl),
            requireContext().getString(R.string.action_update_exercise),
            requireContext().getString(R.string.action_delete_exercise))
        )

        addBtn.setOnClickListener { AddEditMGExerciseDialog(selectedMuscleGroup!!.id).showDialog() }
    }

    override fun updateAdditionalViews() {
        when (panelMode) {
            Mode.SELECT_MUSCLE_GROUP -> {
                title.visibility = View.VISIBLE
                title.text = requireContext().getString(R.string.select_muscle_group_lbl)
                actionSpinner.visibility = View.GONE
            }
            Mode.SELECT_EXERCISE -> {
                title.visibility = View.GONE
                actionSpinner.visibility = View.VISIBLE
            }
        }
    }

    override fun onExerciseSelectCallback(model: MGExerciseModel) {
        // Set the on select callback
        if (actionSpinner.selectedItemPosition == 0) {
            Utils.showToast(R.string.error_select_exercise_action)
            return
        }

        if (actionSpinner.selectedItemPosition == actionSpinnerEditExIndex) {
            // Edit exercise
            AddEditMGExerciseDialog(selectedMuscleGroup!!.id, model).showDialog()

        } else {
            // Delete exercise
            val dialog = DialogAskQuestion(DialogAskQuestion.Question.DELETE_MG_EXERCISE)

            dialog.setYesCallback {
                ExerciseRepository().deleteExercise(model.id, onSuccess = { mGExercises ->
                    // Re-populate on success
                    dialog.closeDialog()
                    populateExercises(mGExercises, false)
                })
            }

            dialog.showDialog(model)
        }
    }
}
