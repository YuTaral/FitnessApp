package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.utils.Utils

/** Add / Edit Workout dialog to hold the logic for add / edit workout */
class AddEditWorkoutDialog {
    companion object {

        /** Show the dialog
         * @param onSave callback to execute on Save button click
         */
        @SuppressLint("InflateParams")
        fun showDialog(onSave: (WorkoutModel) -> Unit) {
            // Inflate the dialog layout
            val inflater = LayoutInflater.from(Utils.getContext())
            val dialogView = inflater.inflate(R.layout.add_edit_workout_dialog, null)

            // Find the views
            val closeIcon = dialogView.findViewById<ImageView>(R.id.dialog_close)
            val name = dialogView.findViewById<EditText>(R.id.workout_name_txt)

            // Create the dialog
            val dialogBuilder = AlertDialog.Builder(Utils.getContext())
            dialogBuilder.setView(dialogView)
                .setPositiveButton(Utils.getContext().getText(R.string.save_btn), null)
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
                if (name.text.isEmpty()) {
                    Utils.validationFailed(name, R.string.error_msg_enter_ex_name)
                    return@setOnClickListener
                }

                // Add the workout
                alertDialog.dismiss()
                onSave(WorkoutModel(name.text.toString(), mutableListOf()))
            }

            // Add click listener for the close button
            closeIcon.setOnClickListener {
                alertDialog.dismiss()
            }
        }
    }
}
