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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.MuscleGroupRecyclerAdapter
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.MuscleGroupRepository
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Add / Edit Workout dialog to hold the logic for add / edit workout
 * @param add true if dialog mode is add (adding workout), false if editing exercise
 * @param workoutModel null by default, not null when the dialog is opened only to confirm
 * the workout name when starting workout from Template
 */
@SuppressLint("InflateParams")
class AddEditWorkoutDialog(add: Boolean, workoutModel: WorkoutModel? = null) {
    private var dialogView: View
    private var title: TextView
    private var closeIcon: ImageView
    private var name: EditText
    private var muscleGroupsLbl: TextView
    private var muscleGroupsRecycler: RecyclerView
    private var saveBtn: Button
    private var deleteBtn: Button
    private var addMode: Boolean
    private var template: WorkoutModel?

    /** Dialog initialization */
    init {
        addMode = add
        template = workoutModel

        // Inflate the dialog layout
        dialogView = LayoutInflater.from(Utils.getContext())
            .inflate(R.layout.add_edit_workout_dialog, null)

        // Find the views
        title = dialogView.findViewById(R.id.add_workout_title)
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        name = dialogView.findViewById(R.id.workout_name_txt)
        muscleGroupsLbl = dialogView.findViewById(R.id.muscle_groups_txt)
        muscleGroupsRecycler = dialogView.findViewById(R.id.muscle_groups_recycler)
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
        if (addMode) {
           var enableMGSelection = true

            if (template != null) {
                // If we only need to confirm the Workout name, set the name
                // and disable the muscle groups
                name.setText(template!!.name)
                enableMGSelection = false
            }

            populateMuscleGroups(muscleGroupsRecycler, enableMGSelection)
        } else {
            title.text = Utils.getContext().getString(R.string.edit_workout_panel_title)
            deleteBtn.visibility = View.VISIBLE
            populateDialog(name, muscleGroupsRecycler)
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

    /** Fetches the Muscle Groups and populates the dialog
     * @param muscleGroupsRecycler the recycler view
     * @param enable whether to enable Muscle Group selection
     */
    private fun populateMuscleGroups(muscleGroupsRecycler: RecyclerView, enable: Boolean) {
        muscleGroupsRecycler.layoutManager = LinearLayoutManager(Utils.getContext())

        if (template != null) {
            // Populate the data by filtering the selected muscle groups
            muscleGroupsRecycler.adapter =
                MuscleGroupRecyclerAdapter(template!!.muscleGroups.filter { it.checked }.toMutableList(), enable)
        } else {
            // Fetch the muscle groups and populate the data
            MuscleGroupRepository().getMuscleGroups(onSuccess = { muscleGroups ->
                muscleGroupsRecycler.adapter = MuscleGroupRecyclerAdapter(muscleGroups, enable)
            })
        }
    }

    /** Populates the dialog when mode is edit
     * @param name the name view
     * @param muscleGroupsRecycler the recycler view
     */
    private fun populateDialog(name: EditText, muscleGroupsRecycler: RecyclerView) {
        if (StateEngine.workout != null) {
            name.setText(StateEngine.workout!!.name)
            muscleGroupsRecycler.layoutManager = LinearLayoutManager(Utils.getContext())
            muscleGroupsRecycler.adapter = MuscleGroupRecyclerAdapter(StateEngine.workout!!.muscleGroups, true)
        }
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
        if (addMode) {
            val newWorkout: WorkoutModel

            if (template == null) {
                // Create new workout
                newWorkout = WorkoutModel(0, name.text.toString(), false, mutableListOf(), getSelMuscleGroups())
            } else {
                // Create new workout from the template, filtering only the selected muscle groups
                newWorkout = template!!
                newWorkout.muscleGroups = newWorkout.muscleGroups.filter { it.checked }.toMutableList()
            }

            WorkoutRepository().addWorkout(newWorkout, onSuccess = { workout ->
                alertDialog.dismiss()
                Utils.showToast(R.string.workout_added)
                StateEngine.panelAdapter.displayWorkoutPanel(workout, true)
            })
        } else {
            WorkoutRepository().editWorkout(WorkoutModel(StateEngine.workout!!.id, name.text.toString(), false, mutableListOf(), getSelMuscleGroups()),
                onSuccess = { workout ->
                    alertDialog.dismiss()
                    Utils.showToast(R.string.workout_updated)
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
            Utils.showToast(R.string.workout_deleted)
            alertDialog.dismiss()
            StateEngine.workout = null
            StateEngine.panelAdapter.displayMainPanel(true)
        })
    }

    /** Return the Selected Muscle = Groups as mutable list */
    private fun getSelMuscleGroups(): MutableList<MuscleGroupModel> {
        return (muscleGroupsRecycler.adapter as MuscleGroupRecyclerAdapter).getSelectedMuscleGroups()
    }
}
