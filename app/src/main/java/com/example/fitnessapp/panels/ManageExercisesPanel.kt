package com.example.fitnessapp.panels

import android.view.View
import android.widget.AdapterView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.CustomSpinnerAdapter
import com.example.fitnessapp.dialogs.AddEditMGExerciseDialog
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.dialogs.DefaultValuesDialog
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.network.repositories.UserProfileRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Manage Exercises Panel class to implement the logic for managing(CRUD operations)
 * muscle group exercises
 */
class ManageExercisesPanel(mode: Mode): BaseExercisePanel(mode) {
    override var id: Long = Constants.PanelUniqueId.MANAGE_EXERCISE.ordinal.toLong()
    override var layoutId: Int = R.layout.exercise_panel
    override var panelIndex: Int = 2
    override var titleId: Int = R.string.manage_exercises_lbl
    override var onlyForUser: String = "Y"
    override var noExercisesStringId: Int = R.string.no_exercise_to_edit_lbl

    /** Enum representing the actions from the action spinner */
    private enum class SpinnerActions(private val stringId: Int) {
        UPDATE_EXERCISE(R.string.action_update_exercise),
        DELETE_EXERCISE(R.string.action_delete_exercise),
        CHANGE_EXERCISE_DEFAULT_VALUES(R.string.action_exercise_default_values);

        override fun toString(): String {
            return Utils.getContext().getString(stringId)
        }
    }

    private var isSpinnerInitialized = false

    override fun findViews() {
        super.findViews()
        actionSpinner = panel.findViewById(R.id.exercise_action_spinner)
    }

    override fun populatePanel() {
        super.populatePanel()
        actionSpinner.adapter = CustomSpinnerAdapter(panel.context, false, listOf(
            SpinnerActions.UPDATE_EXERCISE.toString(),
            SpinnerActions.DELETE_EXERCISE.toString(),
            SpinnerActions.CHANGE_EXERCISE_DEFAULT_VALUES.toString()),
        )
    }

    override fun addClickListeners() {
        super.addClickListeners()
        addBtn.setOnClickListener { AddEditMGExerciseDialog(requireContext(), selectedMuscleGroup!!.id).show() }

        // Fetch different exercises when item changes
        actionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!isSpinnerInitialized) {
                    // Ignore the initial call which is triggered when the spinner is initialized
                    // with the adapter
                    isSpinnerInitialized = true
                    return
                }

                // Assume we must use "Y" when fetching the exercise
                // which will fetch only the user defined exercises. Operations update and delete
                // are valid only for those exercises
                var onlyForUserParam = onlyForUser

                if (position == SpinnerActions.CHANGE_EXERCISE_DEFAULT_VALUES.ordinal) {
                    // Operation change default exercise values are valid for both user
                    // and default exercises
                    onlyForUserParam = "N"
                }

                // Re-populate the exercises
                ExerciseRepository().getMuscleGroupExercises(selectedMuscleGroup!!.id, onlyForUserParam, onSuccess = { exercises ->
                    populateExercises(exercises, true)
                })
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
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
        when (actionSpinner.selectedItemPosition) {
            SpinnerActions.UPDATE_EXERCISE.ordinal -> {
                // Edit exercise
                AddEditMGExerciseDialog(requireContext(), selectedMuscleGroup!!.id, model).show()
            }
            SpinnerActions.DELETE_EXERCISE.ordinal -> {
                // Delete exercise
                val dialog = AskQuestionDialog(requireContext(), AskQuestionDialog.Question.DELETE_MG_EXERCISE, model)

                dialog.setLeftButtonCallback {
                    ExerciseRepository().deleteExercise(model.id, onSuccess = { mGExercises ->
                        // Re-populate on success
                        dialog.dismiss()
                        populateExercises(mGExercises, false)
                    })
                }

                dialog.show()
            }
            SpinnerActions.CHANGE_EXERCISE_DEFAULT_VALUES.ordinal -> {
                // Open user default values to override the default values for this exercise
                UserProfileRepository().getUserDefaultValues(model.id, onSuccess = { values ->
                    DefaultValuesDialog(requireContext(), values, null).show()
                })
            }
        }
    }
}
