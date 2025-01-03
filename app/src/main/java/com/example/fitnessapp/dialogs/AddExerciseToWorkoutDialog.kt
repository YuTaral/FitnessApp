package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.example.fitnessapp.R
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.models.UserDefaultValuesModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.Utils

/** Add/Edit Exercise dialog to implement the logic for
 * adding exercise / updating exercise as part of workout
 */
@SuppressLint("InflateParams")
class AddExerciseToWorkoutDialog(ctx: Context, exercise: MGExerciseModel, values: UserDefaultValuesModel): BaseDialog(ctx) {
    override var layoutId = R.layout.dialog_add_exercise
    override var dialogTitleId = R.string.add_exercise_lbl

    private var exerciseToAdd = exercise
    private var defaultValues = values

    private lateinit var name: EditText
    private lateinit var sets: EditText
    private lateinit var reps: EditText
    private lateinit var weight: EditText
    private lateinit var exerciseCompleted: CheckBox
    private lateinit var saveBtn: Button

    override fun findViews() {
        super.findViews()

        name = dialog.findViewById(R.id.exercise_name)
        sets = dialog.findViewById(R.id.exercise_sets)
        reps = dialog.findViewById(R.id.set_reps)
        weight = dialog.findViewById(R.id.exercise_weight)
        exerciseCompleted = dialog.findViewById(R.id.complete_exercise)
        saveBtn = dialog.findViewById(R.id.save_btn)
    }

    override fun populateDialog() {
        name.setText(exerciseToAdd.name)

        if (defaultValues.sets > 0) {
            sets.setText(defaultValues.sets.toString())
        }

        if (defaultValues.reps > 0) {
            reps.setText(defaultValues.reps.toString())
        }

        exerciseCompleted.isChecked = defaultValues.completed

        if (defaultValues.weight > 0) {
            weight.setText(Utils.formatDouble(defaultValues.weight))
        }

    }

    override fun addClickListeners() {
        super.addClickListeners()

        saveBtn.setOnClickListener { save() }
    }

    /** Executed on Save button click */
    private fun save() {
        val exercise = validateExercise() ?: return
        ExerciseRepository().addExerciseToWorkout(exercise, onSuccess = { workout ->
            dismiss()
            Utils.getPanelAdapter().refreshWorkoutPanel(workout, true)
        })
    }

    /** Validate the data in the dialog when save is clicked and exercise is being added to the workout */
    private fun validateExercise(): ExerciseModel? {
        val exerciseName = name.text.toString()
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

        // Validate Name
        if (exerciseName.isEmpty()) {
            Utils.validationFailed(name, R.string.error_msg_enter_ex_name)
            return null
        }

        val model = MuscleGroupModel(exerciseToAdd.muscleGroupId)

        // Validation passed
        return ExerciseModel(exerciseName, model, exerciseSets, setReps,
                                exerciseWeight, exerciseCompleted.isChecked, exerciseToAdd.id)
    }
}