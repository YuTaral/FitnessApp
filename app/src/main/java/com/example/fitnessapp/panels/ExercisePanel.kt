package com.example.fitnessapp.panels

import android.view.View
import com.example.fitnessapp.R
import com.example.fitnessapp.dialogs.AddEditMGExerciseDialog
import com.example.fitnessapp.dialogs.AddExerciseToWorkoutDialog
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.network.repositories.UserProfileRepository
import com.example.fitnessapp.utils.Constants

/** Exercise Panel class to implement the logic for selecting exercises for workout */
class ExercisePanel(mode: Mode): BaseExercisePanel(mode) {
    override var id: Long = Constants.PanelUniqueId.SELECT_EXERCISE.ordinal.toLong()
    override var layoutId: Int = R.layout.panel_exercise
    override var titleId: Int = R.string.exercise_lbl
    override var onlyForUser: String = "N"
    override var noExercisesStringId: Int = R.string.no_exercise_lbl

    override fun addClickListeners() {
        super.addClickListeners()
        addBtn.setOnClickListener { AddEditMGExerciseDialog(requireContext(), selectedMuscleGroup!!.id).show() }
    }

    override fun updateAdditionalViews() {
        actionSpinner.visibility = View.GONE

        when (panelMode) {
            Mode.SELECT_MUSCLE_GROUP -> {
                title.text = requireContext().getString(R.string.select_muscle_group_lbl)
            }
            Mode.SELECT_EXERCISE -> {
                title.text = requireContext().getString(R.string.select_exercise_lbl)
            }
        }
    }

    override fun onExerciseSelectCallback(model: MGExerciseModel) {
        // Fetch the default values for this exercise and open the add exercise dialog
        UserProfileRepository().getUserDefaultValues(model.id, onSuccess = { values ->
            AddExerciseToWorkoutDialog(requireContext(), model, values).show()
        })
    }
}
