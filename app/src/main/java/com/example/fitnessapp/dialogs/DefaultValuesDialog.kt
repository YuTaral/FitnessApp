package com.example.fitnessapp.dialogs

import android.content.Context
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.CustomSpinnerAdapter
import com.example.fitnessapp.models.UserDefaultValuesModel
import com.example.fitnessapp.models.WeightUnitModel
import com.example.fitnessapp.network.repositories.UserProfileRepository
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Utils

/** DefaultValuesDialog to enter user default values for exercise (sets, reps and weights) */
class DefaultValuesDialog(ctx: Context, values: UserDefaultValuesModel, weightUnits: List<WeightUnitModel>?): BaseDialog(ctx) {
    override var layoutId = R.layout.exercise_default_values_dialog
    override var dialogTitleId = R.string.exercise_default_values_lbl

    private var weightUnitsData = weightUnits
    private var defaultValues = values

    private lateinit var sets: EditText
    private lateinit var reps: EditText
    private lateinit var weight: EditText
    private lateinit var completed: CheckBox
    private lateinit var weightUnit: Spinner
    private lateinit var saveBtn: Button

    override fun findViews() {
        super.findViews()

        sets = dialogView.findViewById(R.id.exercise_sets)
        reps = dialogView.findViewById(R.id.set_reps)
        weight = dialogView.findViewById(R.id.exercise_weight)
        completed = dialogView.findViewById(R.id.complete_exercise)
        weightUnit = dialogView.findViewById(R.id.weight_unit)
        saveBtn = dialogView.findViewById(R.id.save_btn)
    }

    override fun populateDialog() {
        val ctx = Utils.getActivity()
        var spinnerDefaultIndex: Int

        if (defaultValues.sets > 0) {
            sets.setText(defaultValues.sets.toString())
        }

        if (defaultValues.reps > 0) {
            reps.setText(defaultValues.reps.toString())
        }

        completed.isChecked = defaultValues.completed

        if (defaultValues.weight > 0) {
            weight.setText(Utils.formatDouble(defaultValues.weight))
        }

        // Create the units displayed in the spinner
        // If we are editing non-exercise specific values, display the data from the weightUnitsData
        // If we are editing values for specific exercise, just display the user default weight unit,
        // as we cannot change it - the spinner must be disabled.
        val units: MutableList<String> = mutableListOf()

        if (defaultValues.mGExerciseId == 0L) {
            units.addAll(weightUnitsData!!.map { it.text })
        } else {
            units.add(AppStateManager.user!!.defaultValues.weightUnit.text)

            // Add opacity to make sure the spinner looks like it's disabled
            weightUnit.alpha = 0.5f
            weightUnit.isEnabled = false
        }

        weightUnit.adapter = CustomSpinnerAdapter(ctx, false, units)

        spinnerDefaultIndex = (weightUnit.adapter as CustomSpinnerAdapter).getItemIndex(defaultValues.weightUnit.text)

        if (spinnerDefaultIndex < 0) {
            spinnerDefaultIndex = 0
        }

        weightUnit.setSelection(spinnerDefaultIndex)
    }

    override fun addClickListeners() {
        super.addClickListeners()

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

        // Assume we are editing values for specific exercise, in this case weightUnit cannot be changed
        var selectedWeightUnit: WeightUnitModel? = AppStateManager.user!!.defaultValues.weightUnit

        if (defaultValues.mGExerciseId == 0L) {
            // If we are editing non-exercise specific values,
            // get the selected weight unit as it can be changed
            selectedWeightUnit = weightUnitsData!!.find { it.text == weightUnit.selectedItem.toString() }
        }

        val model = UserDefaultValuesModel(defaultValues.id,
                    exerciseSets, setReps, exerciseWeight, completed.isChecked, selectedWeightUnit!!, defaultValues.mGExerciseId)

        UserProfileRepository().updateUserDefaultValues(model, onSuccess = { response ->
            dismiss()

            // If we just edited default user value, it is returned to update the StateEngine
            if (response.data.isNotEmpty() && defaultValues.mGExerciseId == 0L) {
                // Store the old weight unit
                val oldWeightUnit = defaultValues.weightUnit.id
                val newValues = UserDefaultValuesModel(response.data[0])

                // Update the user if we have just updated the default values, not
                // exercise specific default values
                if (defaultValues.mGExerciseId == 0L) {
                    // Simulate user update to trigger update in the shared prefs to update the
                    // default values there
                    val newUser = AppStateManager.user
                    newUser!!.defaultValues = newValues

                    AppStateManager.user = newUser

                    // If weight unit changed, refresh the workouts / workout to update the displayed unit
                    if (newValues.weightUnit.id != oldWeightUnit) {
                        AppStateManager.panelAdapter!!.refreshIfUnitChanged()
                    }
                }
            }
        })
    }
}