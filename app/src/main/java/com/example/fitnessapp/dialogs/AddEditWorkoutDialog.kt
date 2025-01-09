package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import com.example.fitnessapp.R
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.utils.Utils

/** Add / Edit Workout dialog to implement the logic for add / edit workout
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

    override var layoutId = R.layout.dialog_add_edit_workout
    override var dialogTitleId = R.string.add_workout_panel_title

    private var dialogMode: Mode = mode
    private var template: WorkoutModel? = workoutModel

    private lateinit var name: EditText
    private lateinit var notes: EditText
    private lateinit var startEndTimeContainer: ConstraintLayout
    private lateinit  var startLbl: TextView
    private lateinit  var endLbl: TextView
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
        super.findViews()

        closeIcon = dialog.findViewById(R.id.dialog_close)
        name = dialog.findViewById(R.id.workout_name_txt)
        notes = dialog.findViewById(R.id.notes)
        startEndTimeContainer = dialog.findViewById(R.id.start_end_date_time_container)
        startLbl = dialog.findViewById(R.id.workout_start_date_time_txt)
        endLbl = dialog.findViewById(R.id.workout_finish_date_time_txt)
        saveBtn = dialog.findViewById(R.id.save_btn)
        deleteBtn = dialog.findViewById(R.id.delete_btn)
    }

    override fun populateDialog() {
        if (dialogMode == Mode.ADD) {

            if (template != null) {
                name.setText(template!!.name)

                if (template!!.notes.isNotEmpty()) {
                    notes.setText(template!!.notes)
                }
            }

        } else {
            title.text = Utils.getActivity().getString(R.string.edit_workout_panel_title)
            setEditModeButtons()

            name.setText(AppStateManager.workout!!.name)
            notes.setText(AppStateManager.workout!!.notes)

            // Show start and end time
            startEndTimeContainer.visibility = View.VISIBLE
            startLbl.text = Utils.defaultFormatDateTime(AppStateManager.workout!!.startDateTime!!)

            if (AppStateManager.workout!!.finishDateTime == null)  {
                endLbl.visibility = View.GONE
            } else {
                endLbl.text = Utils.defaultFormatDateTime(AppStateManager.workout!!.finishDateTime!!)
            }
        }
    }

    override fun addClickListeners() {
        super.addClickListeners()

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
                WorkoutModel(0, name.text.toString(), false, mutableListOf(), notes.text.toString())
            } else {
                // Create new workout from the template
                WorkoutModel(0, name.text.toString(), true, template!!.exercises, notes.text.toString())
            }

            WorkoutRepository().addWorkout(newWorkout, onSuccess = { workout ->
                dismiss()
                Utils.getPanelAdapter().refreshWorkoutPanel(workout, true)
            })
        } else {
            WorkoutRepository().updateWorkout(WorkoutModel(AppStateManager.workout!!.id, name.text.toString(), false, mutableListOf(), notes.text.toString()),
                onSuccess = { workout ->
                    dismiss()
                    Utils.getPanelAdapter().refreshWorkoutPanel(workout, true)
                })
        }
    }

    /** Executed on Delete button click */
    private fun delete() {
        val dialog = AskQuestionDialog(Utils.getActivity(), AskQuestionDialog.Question.DELETE_WORKOUT, AppStateManager.workout!!)

        dialog.setConfirmButtonCallback {
            // Send a request to delete the workout
            WorkoutRepository().deleteWorkout(AppStateManager.workout!!.id, onSuccess = {
                dialog.dismiss()
                dismiss()
                AppStateManager.workout = null
                Utils.getPanelAdapter().refreshMainPanel()
            })
        }

        dialog.show()
    }
}
