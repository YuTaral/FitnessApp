package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Add/Edit Exercise dialog to hold the logic for
 * adding exercise / updating exercise as part of workout
 */
@SuppressLint("InflateParams")
class AddExerciseFromWorkoutDialog(exercise: MGExerciseModel) {
    private var exerciseToAdd: MGExerciseModel
    private var dialogView: View
    private lateinit var alertDialog: AlertDialog
    private var closeIcon: ImageView
    private var name: EditText
    private var sets: EditText
    private var reps: EditText
    private var weight: EditText
    private var exerciseCompleted: CheckBox
    private var saveBtn: Button

    /** Dialog initialization */
    init {
        exerciseToAdd = exercise

        // Inflate the dialog layout
        dialogView = LayoutInflater.from(Utils.getContext()).inflate(R.layout.add_exercise_dialog, null)

        // Find the views
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        name = dialogView.findViewById(R.id.exercise_name)
        sets = dialogView.findViewById(R.id.exercise_sets)
        reps = dialogView.findViewById(R.id.set_reps)
        weight = dialogView.findViewById(R.id.exercise_weight)
        exerciseCompleted = dialogView.findViewById(R.id.complete_exercise)
        saveBtn = dialogView.findViewById(R.id.save_btn)
    }

    /** Show the dialog */
    fun showDialog() {
        // Create the dialog
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
        dialogBuilder.setView(dialogView).setCancelable(false)
        alertDialog = dialogBuilder.create()

        name.setText(exerciseToAdd.name)

        // Add button click listeners
        saveBtn.setOnClickListener { save() }
        closeIcon.setOnClickListener { alertDialog.dismiss() }

        // Show the dialog
        alertDialog.show()
    }

    /** Executed on Save button click */
    private fun save() {
        val exercise = validateExercise() ?: return
        ExerciseRepository().addExerciseToWorkout(exercise, onSuccess = { workout ->
            alertDialog.dismiss()
            StateEngine.panelAdapter.displayWorkoutPanel(workout, true)
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

        // Create MuscleGroup object only with id, that's the variable which is needed
        // server side in order to create Exercise record
        val model = MuscleGroupModel(exerciseToAdd.muscleGroupId)

        // Validation passed
        return ExerciseModel(exerciseName, model, exerciseSets, setReps, exerciseWeight, exerciseCompleted.isChecked)
    }
}