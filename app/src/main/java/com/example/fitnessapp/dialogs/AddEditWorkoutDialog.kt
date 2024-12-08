package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import com.example.fitnessapp.R
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Add / Edit Workout dialog to hold the logic for add / edit workout
 * @param mode the dialog mode
 * @param workoutModel null by default, not null when the dialog is opened only to confirm
 * the workout name when starting workout from Template
 */
@SuppressLint("InflateParams")
class AddEditWorkoutDialog(ctx: Context, mode: Mode, workoutModel: WorkoutModel? = null): BaseDialog(ctx) {
    /** The dialog mode - add / edit */
    enum class Mode {
        ADD,
        EDIT
    }

    override var layoutId = R.layout.add_edit_workout_dialog

    private var dialogMode: Mode = mode
    private var template: WorkoutModel? = workoutModel

    private lateinit  var title: TextView
    private lateinit var name: EditText
    private lateinit var saveBtn: Button
    private lateinit var deleteBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Open the keyboard once the dialog is shown
        name.requestFocus()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun findViews() {
        title = dialogView.findViewById(R.id.add_workout_title)
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        name = dialogView.findViewById(R.id.workout_name_txt)
        saveBtn = dialogView.findViewById(R.id.save_btn)
        deleteBtn = dialogView.findViewById(R.id.delete_btn)
    }

    override fun populateDialog() {
        if (dialogMode == Mode.ADD) {

            if (template != null) {
                // If we only need to confirm the Workout name, set the name
                // and disable the muscle groups
                name.setText(template!!.name)
            }

        } else {
            title.text = Utils.getContext().getString(R.string.edit_workout_panel_title)
            setEditModeButtons()

            if (StateEngine.workout != null) {
                name.setText(StateEngine.workout!!.name)
            }
        }
    }

    override fun addClickListeners() {
        closeIcon.setOnClickListener { dismiss() }
        saveBtn.setOnClickListener { save() }
        if (deleteBtn.visibility == View.VISIBLE) {
            deleteBtn.setOnClickListener { delete() }
        }
    }

    /** Sets the buttons visibility and styles for Edit Mode*/
    private fun setEditModeButtons() {
        deleteBtn.visibility = View.VISIBLE

        var layoutParams = deleteBtn.layoutParams as LayoutParams
        layoutParams.startToStart = LayoutParams.PARENT_ID
        layoutParams.endToStart = R.id.save_btn
        deleteBtn.setBackgroundResource(R.drawable.background_right_border)
        deleteBtn.layoutParams = layoutParams

        layoutParams = saveBtn.layoutParams as LayoutParams
        layoutParams.startToStart = LayoutParams.UNSET
        layoutParams.startToEnd = R.id.delete_btn
        saveBtn.setBackgroundResource(R.drawable.background_top_border)
    }

    /** Executed on Save button click */
    private fun save() {
        // Validate
        if (name.text.isEmpty()) {
            Utils.validationFailed(name, R.string.error_msg_enter_workout_name)
            return
        }

        // Add / edit the workout
        if (dialogMode == Mode.ADD) {
            val newWorkout: WorkoutModel = if (template == null) {
                // Create new workout
                WorkoutModel(0, name.text.toString(), false, mutableListOf())
            } else {
                // Create new workout from the template
                WorkoutModel(0, name.text.toString(), true, template!!.exercises)
            }

            WorkoutRepository().addWorkout(newWorkout, onSuccess = { workout ->
                dismiss()
                StateEngine.panelAdapter.displayWorkoutPanel(workout, true)
            })
        } else {
            WorkoutRepository().editWorkout(WorkoutModel(StateEngine.workout!!.id, name.text.toString(), false, mutableListOf()),
                onSuccess = { workout ->
                    dismiss()
                    StateEngine.panelAdapter.displayWorkoutPanel(workout, true)
                })
        }
    }

    /** Executed on Delete button click */
    private fun delete() {
        val dialog = AskQuestionDialog(Utils.getContext(), AskQuestionDialog.Question.DELETE_WORKOUT, StateEngine.workout!!)

        dialog.setYesCallback {
            // Send a request to delete the workout
            WorkoutRepository().deleteWorkout(StateEngine.workout!!.id, onSuccess = {
                dialog.dismiss()
                dismiss()
                StateEngine.workout = null
                StateEngine.panelAdapter.displayMainPanel(true)
            })
        }

        dialog.show()
    }
}
