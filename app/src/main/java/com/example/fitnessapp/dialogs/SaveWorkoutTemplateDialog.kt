package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutTemplateRepository
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.utils.Utils

/** Save workout template dialog to implement the logic to save the current workout as template */
@SuppressLint("InflateParams")
class SaveWorkoutTemplateDialog(ctx: Context): BaseDialog(ctx) {
    override var layoutId = R.layout.dialog_save_workout_template
    override var dialogTitleId = R.string.save_template_lbl

    private lateinit var templateName: EditText
    private lateinit var exercises: TextView
    private lateinit var saveBtn: Button

    override fun findViews() {
        super.findViews()

        templateName = dialog.findViewById(R.id.template_name_txt)
        exercises = dialog.findViewById(R.id.workout_exercises_summary_txt)
        saveBtn = dialog.findViewById(R.id.save_btn)
    }

    override fun populateDialog() {
        templateName.setText(AppStateManager.workout!!.name)
        exercises.text = AppStateManager.workout!!.exercises.joinToString (separator = ", ") { it.name }
    }

    override fun addClickListeners() {
        super.addClickListeners()

        saveBtn.setOnClickListener { save() }
    }

    /** Send a request to create the workout template */
    private fun save() {
        // Validate
        if (templateName.text.isEmpty()) {
            Utils.validationFailed(templateName, R.string.error_msg_enter_template_name)
            return
        }

        // Create template, changing the name and adding only the selected Muscle Groups
        val template = WorkoutModel(0, templateName.text.toString(), true, AppStateManager.workout!!.exercises)

        WorkoutTemplateRepository().addWorkoutTemplate(template, onSuccess =  {
            dismiss()
        })
    }
}