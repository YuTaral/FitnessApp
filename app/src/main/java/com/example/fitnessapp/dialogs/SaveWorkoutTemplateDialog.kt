package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutTemplateRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Save workout template dialog to hold the logic to save the current workout as template */
@SuppressLint("InflateParams")
class SaveWorkoutTemplateDialog {
    private var dialogView: View
    private var templateName: EditText
    private var exercises: TextView
    private var closeIcon: ImageView
    private var saveBtn: Button

    /** Dialog initialization */
    init {
        dialogView = LayoutInflater.from(Utils.getContext()).inflate(R.layout.save_workout_template_dialog, null)

        // Find the views
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        templateName = dialogView.findViewById(R.id.template_name_txt)
        exercises = dialogView.findViewById(R.id.workout_exercises_summary_txt)
        saveBtn = dialogView.findViewById(R.id.save_btn)
    }

    /** Show the dialog */
    fun showDialog() {
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
        dialogBuilder.setView(dialogView).setCancelable(false)
        val alertDialog = dialogBuilder.create()

        // Populate the data
        templateName.setText(StateEngine.workout!!.name)
        exercises.text = StateEngine.workout!!.exercises.joinToString (separator = ", ") { it.name }

        // Add button click listeners
        closeIcon.setOnClickListener { alertDialog.dismiss() }
        saveBtn.setOnClickListener { save(alertDialog) }

        alertDialog.show()
    }

    /** Sends a request to create the workout template */
    private fun save(alertDialog: AlertDialog) {
        // Validate
        if (templateName.text.isEmpty()) {
            Utils.validationFailed(templateName, R.string.error_msg_enter_template_name)
            return
        }

        // Create template, changing the name and adding only the selected Muscle Groups
        val template = WorkoutModel(0, templateName.text.toString(), true, StateEngine.workout!!.exercises)

        WorkoutTemplateRepository().addWorkoutTemplate(template, onSuccess =  {
            alertDialog.dismiss()
        })
    }
}