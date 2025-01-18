package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutTemplateRepository
import com.example.fitnessapp.utils.Utils
import com.google.android.material.textfield.TextInputLayout

/** Add / edit workout template dialog to implement the logic to save the workout as template
 * or edit existing template
 * */
@SuppressLint("InflateParams")
class AddEditTemplateDialog(ctx: Context, w: WorkoutModel, m: Mode): BaseDialog(ctx) {
    override var layoutId = R.layout.dialog_save_workout_template
    override var dialogTitleId = 0

    private lateinit var templateName: EditText
    private lateinit var notes: EditText
    private lateinit var exercises: TextView
    private lateinit var saveBtn: Button

    private var workout = w

    /** The dialog mode */
    enum class Mode {
        ADD,
        UPDATE
    }

    private var mode = m

    override fun findViews() {
        super.findViews()

        templateName = dialog.findViewById<TextInputLayout>(R.id.template_name_txt).editText!!
        notes = dialog.findViewById<TextInputLayout>(R.id.notes).editText!!
        exercises = dialog.findViewById(R.id.workout_exercises_summary_txt)
        saveBtn = dialog.findViewById(R.id.save_btn)
    }

    override fun populateDialog() {
        dialogTitleId = if (mode == Mode.ADD) {
            R.string.save_template_lbl
        } else {
            R.string.update_template_lbl
        }

        title.setText(dialogTitleId)

        templateName.setText(workout.name)
        notes.setText(workout.notes)
        exercises.text = workout.exercises.joinToString (separator = ", ") { it.name }
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

        if (mode == Mode.ADD) {
            // Create template, changing the name and notes
            val template = WorkoutModel(0, templateName.text.toString(), true,
                workout.exercises, notes.text.toString(), null, 0)

            // Mark all sets as uncompleted
            template.exercises.map { e ->
                e.sets.map { it.completed = false }
            }

            WorkoutTemplateRepository().addWorkoutTemplate(template, onSuccess =  {
                dismiss()
                Utils.getPanelAdapter().getManageTemplatesPanel()?.populateTemplates(true)
            })
        } else {
            // Edit the template, changing the name and notes
            val template = WorkoutModel(workout.id, templateName.text.toString(), true,
                workout.exercises, notes.text.toString(), null, 0)

            WorkoutTemplateRepository().updateWorkoutTemplate(template, onSuccess =  {
                dismiss()
                Utils.getPanelAdapter().getManageTemplatesPanel()?.populateTemplates(false)
            })
        }
    }
}