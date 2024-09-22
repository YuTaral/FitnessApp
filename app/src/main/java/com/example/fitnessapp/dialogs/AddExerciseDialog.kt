package com.example.fitnessapp.dialogs

import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.utils.Utils

/** Add Exercise dialog to hold the logic for adding exercise */
class AddExerciseDialog {

    companion object {
        /** Show the dialog
         * @param onSave callback to execute on Save button click
         */
        fun showDialog(onSave: (ExerciseModel) -> Unit) {
            // Inflate the dialog layout
            val inflater = LayoutInflater.from(Utils.getActivity())
            val dialogView = inflater.inflate(R.layout.add_exercise_dialog, null)

            // Find the views
            val closeIcon = dialogView.findViewById<ImageView>(R.id.dialog_close)
            val name = dialogView.findViewById<EditText>(R.id.exercise_name)
            val sets = dialogView.findViewById<EditText>(R.id.exercise_sets)
            val reps = dialogView.findViewById<EditText>(R.id.set_reps)
            val weight = dialogView.findViewById<EditText>(R.id.exercise_weight)

            // Create the dialog
            val dialogBuilder = AlertDialog.Builder(Utils.getActivity())
            dialogBuilder.setView(dialogView)
                         .setPositiveButton(Utils.getActivity().getText(R.string.save_btn), null)
                         .setCancelable(false)

            // Show the dialog
            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            // Open the keyboard once the dialog is shown
            name.requestFocus()
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

            // Override the positive button click listener to prevent it from closing the dialog
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
                // Validate
                val exercise = Utils.validateExercise(name, sets, reps, weight)
                    ?: return@setOnClickListener

                // Add the exercise
                alertDialog.dismiss()
                onSave(exercise)
            }

            // Add click listener for the close button
            closeIcon.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }
}