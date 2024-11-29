package com.example.fitnessapp.panels

import android.view.View
import com.example.fitnessapp.R
import com.example.fitnessapp.dialogs.AddEditMGExerciseDialog
import com.example.fitnessapp.dialogs.AddExerciseFromWorkoutDialog
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.utils.Constants

/** Class to hold the logic for Exercise Panel */
class ExercisePanel(mode: Mode): BaseExercisePanel(mode) {
    override var id: Long = Constants.PanelUniqueId.SELECT_EXERCISE.ordinal.toLong()
    override var layoutId: Int = R.layout.exercise_panel
    override var panelIndex: Int = 2
    override var titleId: Int = R.string.exercise_lbl
    override var onlyForUser: String = "N"
    override var noExercisesStringId: Int = R.string.no_exercise_lbl

    override fun additionalPanelInitialization() {
        addBtn.setOnClickListener { AddEditMGExerciseDialog(selectedMuscleGroup!!.id).showDialog() }
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
        AddExerciseFromWorkoutDialog(model).showDialog()
    }
}
