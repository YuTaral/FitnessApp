package com.example.fitnessapp.dialogs

import android.content.Context
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.example.fitnessapp.R
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.models.UserDefaultValuesModel
import com.example.fitnessapp.models.WeightUnitModel
import com.example.fitnessapp.network.repositories.UserProfileRepository
import com.example.fitnessapp.utils.Utils
import com.example.fitnessapp.views.CustomSwitchView
import com.google.android.material.textfield.TextInputLayout

/** DefaultValuesDialog to enter user default values for exercise (sets, reps and weights) */
class DefaultValuesDialog(ctx: Context, values: UserDefaultValuesModel, weightUnits: List<WeightUnitModel>?): BaseDialog(ctx) {
    override var layoutId = R.layout.dialog_exercise_default_values
    override var dialogTitleId = R.string.exercise_default_values_lbl

    private var weightUnitsData = weightUnits
    private var defaultValues = values

    private lateinit var sets: EditText
    private lateinit var reps: EditText
    private lateinit var weight: EditText
    private lateinit var rest: EditText
    private lateinit var completed: CheckBox
    private lateinit var weightUnitSwitch: CustomSwitchView
    private lateinit var saveBtn: Button

    override fun findViews() {
        super.findViews()

        sets = dialog.findViewById<TextInputLayout>(R.id.exercise_sets).editText!!
        reps = dialog.findViewById<TextInputLayout>(R.id.set_reps).editText!!
        weight = dialog.findViewById<TextInputLayout>(R.id.exercise_weight).editText!!
        rest = dialog.findViewById<TextInputLayout>(R.id.rest_txt).editText!!
        completed = dialog.findViewById(R.id.complete_exercise)
        weightUnitSwitch = dialog.findViewById(R.id.weigh_unit_selector)
        saveBtn = dialog.findViewById(R.id.save_btn)
    }

    override fun populateDialog() {
        if (defaultValues.sets > 0) {
            sets.setText(defaultValues.sets.toString())
        }

        if (defaultValues.reps > 0) {
            reps.setText(defaultValues.reps.toString())
        }

        if (defaultValues.rest > 0) {
            rest.setText(defaultValues.rest.toString())
        }

        completed.isChecked = defaultValues.completed

        if (defaultValues.weight > 0) {
            weight.setText(Utils.formatDouble(defaultValues.weight))
        }

        if (defaultValues.mGExerciseId > 0L) {
            // Disable switch if we are editing exercise default values
            weightUnitSwitch.disable()
        }

        if (defaultValues.weightUnit.text != weightUnitSwitch.getSelectedText()) {
            // Switch the selected value if it's not the correct one
            weightUnitSwitch.switch()
        }
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
        var setRest = 0

        if (sets.text.toString().isNotEmpty()) {
            exerciseSets = sets.text.toString().toInt()
        }
        if (reps.text.toString().isNotEmpty()) {
            setReps = reps.text.toString().toInt()
        }
        if (weight.text.toString().isNotEmpty()) {
            exerciseWeight = weight.text.toString().toDouble()
        }

        if (rest.text.toString().isNotEmpty()) {
            setRest = rest.text.toString().toInt()
        }

        // Assume we are editing values for specific exercise, in this case weightUnit cannot be changed
        var selectedWeightUnit: WeightUnitModel? = AppStateManager.user!!.defaultValues.weightUnit

        if (defaultValues.mGExerciseId == 0L) {
            // If we are editing non-exercise specific values,
            // get the selected weight unit as it can be changed
            selectedWeightUnit = weightUnitsData!!.find { it.text == weightUnitSwitch.getSelectedText() }
        }

        val model = UserDefaultValuesModel(defaultValues.id, exerciseSets, setReps, exerciseWeight,
                    setRest, completed.isChecked, selectedWeightUnit!!, defaultValues.mGExerciseId)

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
                        Utils.getPanelAdapter().refreshIfUnitChanged()
                    }
                }
            }
        })
    }
}