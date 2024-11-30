package com.example.fitnessapp.dialogs

import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.CustomSpinnerAdapter
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** DefaultValuesDialog to enter user default values for exercise (sets, reps and weights) */
class DefaultValuesDialog(ctx: Context): BaseDialog(ctx) {
    override var layoutId = R.layout.exercise_default_values_dialog

    private lateinit var sets: EditText
    private lateinit var reps: EditText
    private lateinit var weight: EditText
    private lateinit var weightUnit: Spinner
    private lateinit var saveBtn: Button

    override fun findViews() {
        sets = dialogView.findViewById(R.id.exercise_sets)
        reps = dialogView.findViewById(R.id.set_reps)
        weight = dialogView.findViewById(R.id.exercise_weight)
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
            .getItemIndex(StateEngine.user.defaultValues.weightUnit)
        if (spinnerDefaultIndex < 0) {
            spinnerDefaultIndex = 0
        }

        sets.setText(StateEngine.user.defaultValues.sets.toString())
        reps.setText(StateEngine.user.defaultValues.reps.toString())
        weight.setText(StateEngine.user.defaultValues.weight.toString())
        weightUnit.setSelection(spinnerDefaultIndex)
    }

    override fun addClickListeners() {
        saveBtn.setOnClickListener { save() }
    }

    /** Executed on save button click. Used to save the default values */
    private fun save() {

    }
}