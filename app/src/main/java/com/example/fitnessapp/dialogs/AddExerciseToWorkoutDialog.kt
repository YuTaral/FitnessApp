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
import com.google.android.material.textfield.TextInputLayout

/** Add/Edit Exercise dialog to implement the logic for
 * adding exercise / updating exercise as part of workout
 */
@SuppressLint("InflateParams")
class AddExerciseToWorkoutDialog(ctx: Context, exercise: MGExerciseModel, values: UserDefaultValuesModel): BaseDialog(ctx) {
    override var layoutId = R.layout.dialog_add_exercise
    override var dialogTitleId = 0

    private var exerciseToAdd = exercise
    private var defaultValues = values

    private lateinit var notes: EditText
    private lateinit var sets: EditText
    private lateinit var reps: EditText
    private lateinit var weight: EditText
    private lateinit var rest: EditText
    private lateinit var exerciseCompleted: CheckBox
    private lateinit var addBtn: Button

    override fun findViews() {
        super.findViews()

        notes = dialog.findViewById<TextInputLayout>(R.id.notes).editText!!
        sets = dialog.findViewById<TextInputLayout>(R.id.exercise_sets).editText!!
        reps = dialog.findViewById<TextInputLayout>(R.id.set_reps).editText!!
        weight = dialog.findViewById<TextInputLayout>(R.id.exercise_weight).editText!!
        rest = dialog.findViewById<TextInputLayout>(R.id.rest_txt).editText!!
        exerciseCompleted = dialog.findViewById(R.id.complete_exercise)
        addBtn = dialog.findViewById(R.id.add_btn)
    }

    override fun populateDialog() {
        title.text = exerciseToAdd.name

        if (defaultValues.sets > 0) {
            sets.setText(defaultValues.sets.toString())
        }

        if (defaultValues.reps > 0) {
            reps.setText(defaultValues.reps.toString())
        }

        if (defaultValues.rest > 0) {
            rest.setText(defaultValues.rest.toString())
        }

        exerciseCompleted.isChecked = defaultValues.completed

        if (defaultValues.weight > 0) {
            weight.setText(Utils.formatDouble(defaultValues.weight))
        }
    }

    override fun addClickListeners() {
        super.addClickListeners()

        addBtn.setOnClickListener { save() }
    }

    /** Executed on Save button click */
    private fun save() {
        val exercise = createExerciseModel()
        ExerciseRepository().addExerciseToWorkout(exercise, onSuccess = { workout ->
            dismiss()
            Utils.getPanelAdapter().refreshWorkoutPanel(workout, true)
        })
    }

    /** Validate the data in the dialog when save is clicked and exercise is being added to the workout */
    private fun createExerciseModel(): ExerciseModel {
        val exerciseName = title.text.toString()
        var exerciseSets = 0
        var setReps = 0
        var setRest = 0
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

        if (rest.text.toString().isNotEmpty()) {
            setRest = rest.text.toString().toInt()
        }

        val model = MuscleGroupModel(exerciseToAdd.muscleGroupId)

        // Validation passed
        return ExerciseModel(exerciseName, model, exerciseSets, setReps, exerciseWeight, setRest,
                                exerciseCompleted.isChecked, exerciseToAdd.id, notes.text.toString())
    }
}