package com.example.fitnessapp.dialogs

import android.content.Context
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.CustomSpinnerAdapter
import com.example.fitnessapp.models.UserDefaultValuesModel
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** DefaultValuesDialog to enter user default values for exercise (sets, reps and weights) */
class DefaultValuesDialog(ctx: Context): BaseDialog(ctx) {
    override var layoutId = R.layout.exercise_default_values_dialog

    private lateinit var sets: EditText
    private lateinit var reps: EditText
    private lateinit var weight: EditText
    private lateinit var completed: CheckBox
    private lateinit var weightUnit: Spinner
    private lateinit var saveBtn: Button

    override fun findViews() {
        sets = dialogView.findViewById(R.id.exercise_sets)
        reps = dialogView.findViewById(R.id.set_reps)
        weight = dialogView.findViewById(R.id.exercise_weight)
        completed = dialogView.findViewById(R.id.complete_exercise)
        weightUnit = dialogView.findViewById(R.id.weight_unit)
        saveBtn = dialogView.findViewById(R.id.save_btn)
    }

    override fun populateDialog() {
        val ctx = Utils.getContext()
        var spinnerDefaultIndex: Int

        weightUnit.adapter = CustomSpinnerAdapter(ctx, false, listOf(
            ctx.getString(R.string.weight_unit_kg_lbl),
            ctx.getString(R.string.weight_unit_lb_lbl)
        ))

        spinnerDefaultIndex = (weightUnit.adapter as CustomSpinnerAdapter)
            .getItemIndex(StateEngine.user.defaultValues.weightUnitText)
        if (spinnerDefaultIndex < 0) {
            spinnerDefaultIndex = 0
        }

        if (StateEngine.user.defaultValues.sets > 0) {
            sets.setText(StateEngine.user.defaultValues.sets.toString())
        }

        if (StateEngine.user.defaultValues.reps > 0) {
            reps.setText(StateEngine.user.defaultValues.reps.toString())
        }

        completed.isChecked = StateEngine.user.defaultValues.completed

        if (StateEngine.user.defaultValues.weight > 0) {
            weight.setText(Utils.formatDouble(StateEngine.user.defaultValues.weight))
        }

        weightUnit.setSelection(spinnerDefaultIndex)
    }

    override fun addClickListeners() {
        saveBtn.setOnClickListener { save() }
    }

    /** Executed on save button click. Used to save the default values */
    private fun save() {
        var exerciseSets = 0
        var setReps = 0
        var exerciseWeight = 0.0

        if (sets.text.toString().isNotEmpty()) {
            exerciseSets = sets.text.toString().toInt()
        }
        if (reps.text.toString().isNotEmpty()) {
            setReps = reps.text.toString().toInt()
        }
        if (weight.text.toString().isNotEmpty()) {
            exerciseWeight = weight.text.toString().toDouble()
        }

        val model = UserDefaultValuesModel(StateEngine.user.defaultValues.id,
                    exerciseSets, setReps, exerciseWeight, completed.isChecked, weightUnit.selectedItem.toString())

        UserRepository().changeUserDefaultValues(model, onSuccess = { values ->
            dismiss()

            // Store the old weight unit
            val oldWeightUnit = StateEngine.user.defaultValues.weightUnitText

            // Update the user
            StateEngine.user.defaultValues = values

            // If weight unit changed, refresh the workouts / workout to update the displayed unit
            if (values.weightUnitText != oldWeightUnit) {
                StateEngine.panelAdapter.refreshIfUnitChanged()
            }
        })
    }
}