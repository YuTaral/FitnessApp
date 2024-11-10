package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
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
class AddEditWorkoutDialog(mode: Mode, workoutModel: WorkoutModel? = null) {
    /** The dialog mode - add / edit */
    enum class Mode {
        ADD,
        EDIT
    }

    private var dialogMode: Mode = mode
    private var template: WorkoutModel? = workoutModel
    private var dialogView: View
    private var title: TextView
    private var closeIcon: ImageView
    private var name: EditText
    private var saveBtn: Button
    private var deleteBtn: Button

    /** Dialog initialization */
    init {
        // Inflate the dialog layout
        dialogView = LayoutInflater.from(Utils.getContext())
            .inflate(R.layout.add_edit_workout_dialog, null)

        // Find the views
        title = dialogView.findViewById(R.id.add_workout_title)
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        name = dialogView.findViewById(R.id.workout_name_txt)
        saveBtn = dialogView.findViewById(R.id.save_btn)
        deleteBtn = dialogView.findViewById(R.id.delete_btn)
    }

    /** Show the dialog */
    fun showDialog() {
        // Create the dialog
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
        dialogBuilder.setView(dialogView).setCancelable(false)
        val alertDialog = dialogBuilder.create()

        // Populate the dialog and change the views
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

        // Add button click listeners
        closeIcon.setOnClickListener { alertDialog.dismiss() }
        saveBtn.setOnClickListener { save(alertDialog) }
        if (deleteBtn.visibility == View.VISIBLE) {
            deleteBtn.setOnClickListener { delete(alertDialog) }
        }

        // Show the dialog
        alertDialog.show()

        // Open the keyboard once the dialog is shown
        name.requestFocus()
        alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
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

    /** Executed on Save button click
     * @param alertDialog the alert dialog
     */
    private fun save(alertDialog: AlertDialog) {
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
                WorkoutModel(0, name.text.toString(), false, template!!.exercises)
            }

            WorkoutRepository().addWorkout(newWorkout, onSuccess = { workout ->
                alertDialog.dismiss()
                StateEngine.panelAdapter.displayWorkoutPanel(workout, true)
            })
        } else {
            WorkoutRepository().editWorkout(WorkoutModel(StateEngine.workout!!.id, name.text.toString(), false, mutableListOf()),
                onSuccess = { workout ->
                    alertDialog.dismiss()
                    StateEngine.panelAdapter.displayWorkoutPanel(workout, true)
                })
        }
    }

    /** Executed on Delete button click
     * @param alertDialog the alert dialog
     */
    private fun delete(alertDialog: AlertDialog) {
        // Send a request to delete the workout
        WorkoutRepository().deleteWorkout(StateEngine.workout!!.id, onSuccess = {
            alertDialog.dismiss()
            StateEngine.workout = null
            StateEngine.panelAdapter.displayMainPanel(true)
        })
    }
}
