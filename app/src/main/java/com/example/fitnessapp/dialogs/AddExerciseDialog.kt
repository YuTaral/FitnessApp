package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Add Exercise dialog to hold the logic for adding exercise */
@SuppressLint("InflateParams")
class AddExerciseDialog {
    private var dialogView: View
    private var closeIcon: ImageView
    private var name: EditText
    private var sets: EditText
    private var reps: EditText
    private var weight: EditText
    private var saveBtn: Button

    /** Dialog initialization */
    init {
        // Inflate the dialog layout
        dialogView = LayoutInflater.from(Utils.getContext()).inflate(R.layout.add_exercise_dialog, null)

        // Find the views
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        name = dialogView.findViewById(R.id.exercise_name)
        sets = dialogView.findViewById(R.id.exercise_sets)
        reps = dialogView.findViewById(R.id.set_reps)
        weight = dialogView.findViewById(R.id.exercise_weight)
        saveBtn = dialogView.findViewById(R.id.save_btn)
    }

    /** Show the dialog */
    fun showDialog() {
        // Create the dialog
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
        dialogBuilder.setView(dialogView).setCancelable(false)
        val alertDialog = dialogBuilder.create()

        // Add button click listeners
        saveBtn.setOnClickListener { save(alertDialog) }
        closeIcon.setOnClickListener { alertDialog.dismiss() }

        // Show the dialog
        alertDialog.show()

        // Open the keyboard once the dialog is shown
        name.requestFocus()
        alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    /** Executed on Save button click
     * @param alertDialog the alert dialog
     */
    private fun save(alertDialog: AlertDialog){
        // Validate
        val exercise = Utils.validateExercise(name, sets, reps, weight) ?: return

        // Add the exercise
        ExerciseRepository().addExercise(exercise, onSuccess = { workout ->
            alertDialog.dismiss()
            StateEngine.workout = workout
            Utils.getActivity().displayNewWorkoutPanel()
            Utils.showToast(R.string.exercise_updated)
            StateEngine.refreshWorkouts = true
        })
    }
}