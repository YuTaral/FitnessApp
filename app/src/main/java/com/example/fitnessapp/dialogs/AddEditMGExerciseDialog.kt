package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.CustomResponse
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Add/Edit Muscle group Exercise dialog to hold the logic for
 * adding exercise / updating exercise as part of muscle group
 */
@SuppressLint("InflateParams")
class AddEditMGExerciseDialog(mGroupId: Long, exercise: MGExerciseModel? = null) {
    private var muscleGroupId: Long
    private var exerciseToEdit: MGExerciseModel?
    private var dialogView: View
    private var title: TextView
    private var closeIcon: ImageView
    private var name: EditText
    private var description: EditText
    private var addExerciseToWorkout: CheckBox
    private var saveBtn: Button
    private lateinit var alertDialog: AlertDialog

    /** Dialog initialization */
    init {
        muscleGroupId = mGroupId
        exerciseToEdit = exercise

        // Inflate the dialog layout
        dialogView = LayoutInflater.from(Utils.getContext()).inflate(R.layout.add_edit_mgexercise_dialog, null)

        // Find the views
        title = dialogView.findViewById(R.id.add_exercise_dialog_title)
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        name = dialogView.findViewById(R.id.exercise_name)
        description = dialogView.findViewById(R.id.exercise_description)
        addExerciseToWorkout = dialogView.findViewById(R.id.add_exercise_to_workout)
        saveBtn = dialogView.findViewById(R.id.save_btn)
    }

    /** Show the dialog */
    fun showDialog() {
        // Create the dialog
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
        dialogBuilder.setView(dialogView).setCancelable(false)
        alertDialog = dialogBuilder.create()

        // Add button click listeners
        saveBtn.setOnClickListener { save("Y") }
        closeIcon.setOnClickListener { alertDialog.dismiss() }

        // Change the title and populate the data if edit
        if (exerciseToEdit != null) {
            title.setText(R.string.edit_exercise_lbl)
            name.setText(exerciseToEdit!!.name)
            description.setText(exerciseToEdit!!.description)
            addExerciseToWorkout.visibility = View.GONE
        }

        if (StateEngine.panelAdapter.getManageExercisesPanel() != null) {
            // Remove the add exercise to workout checkbox if we are
            // managing the exercises
            addExerciseToWorkout.visibility = View.GONE
        }

        // Show the dialog
        alertDialog.show()

        // Open the keyboard once the dialog is shown
        if (exerciseToEdit == null) {
            name.requestFocus()
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    /** Executed on Save button click */
    private fun save(checkExistingEx: String) {
        val exercise = validateMGExercise() ?: return

        if (exerciseToEdit == null) {
            var updateWorkout = false
            var workoutId = "0"

            if (addExerciseToWorkout.visibility == View.VISIBLE) {
                updateWorkout = addExerciseToWorkout.isChecked && StateEngine.workout != null
            }

            if (updateWorkout) {
                workoutId = StateEngine.workout!!.id.toString()
            }

            // Add the exercise
            ExerciseRepository().addExercise(exercise, workoutId, getOnlyForUserParam(), checkExistingEx,
                onSuccess = { returnData -> onSuccessCallback(updateWorkout, returnData) },
                onFailure = { response  -> onFailureCallback(response) })

        } else {
            // Edit the exercise
            ExerciseRepository().updateExercise(exercise, getOnlyForUserParam(), onSuccess = { returnData ->
                alertDialog.dismiss()

                // Response contains the updated muscle group exercises
                StateEngine.panelAdapter.getIExercisePanel()!!
                    .populateExercises(returnData.map{ MGExerciseModel(it) }, false)
            })

        }
    }

    /** Callback to execute when Add / Edit request was successful
     * @param updateWorkout true if we need to update the workout, false otherwise
     * @param returnData the data returned by the request
     */
    private fun onSuccessCallback(updateWorkout: Boolean, returnData: List<String>) {
        if (updateWorkout) {
            // Response contains the updated workout
            StateEngine.panelAdapter
                .displayWorkoutPanel(WorkoutModel(returnData[0]), true)

        } else {
            // Response contains the updated muscle group exercises
            StateEngine.panelAdapter.getIExercisePanel()!!
                .populateExercises(returnData.map { MGExerciseModel(it) }, false)

        }
        alertDialog.dismiss()
    }

    /** Callback to execute when Add / Edit request failed
     * @param response the response returned by the server
     */
    private fun onFailureCallback(response: CustomResponse) {
        if (response.responseCode == Constants.ResponseCode.EXERCISE_ALREADY_EXISTS.ordinal) {
            // Display dialog asking the user whether to override the existing exercise
            val dialog = DialogAskQuestion(DialogAskQuestion.Question.OVERRIDE_EXISTING_EXERCISE)

            dialog.setYesCallback {
                val existingExercise = MGExerciseModel(response.responseData[0])
                existingExercise.description = description.text.toString()

                ExerciseRepository().updateExercise(existingExercise, getOnlyForUserParam(), onSuccess = { returnData ->
                    // Close the dialogs
                    dialog.closeDialog()
                    alertDialog.dismiss()

                    // Updated the exercises
                    StateEngine.panelAdapter.getIExercisePanel()!!
                        .populateExercises(returnData.map { MGExerciseModel(it) }, false)
                })
            }

            dialog.setNoCallback {
                dialog.closeDialog()
                save("N")
            }

            dialog.showDialog(MGExerciseModel(response.responseData[0]))
        }
    }

    /** Validate the data in the dialog when save is clicked */
    private fun validateMGExercise(): MGExerciseModel? {
        val exerciseName = name.text.toString()

        // Validate Name
        if (exerciseName.isEmpty()) {
            Utils.validationFailed(name, R.string.error_msg_enter_ex_name)
            return null
        }

        // Validation passed
        return if (exerciseToEdit == null) {
            // Return new exercise for creation
            MGExerciseModel(0, exerciseName, description.text.toString(), muscleGroupId)
        } else {
            // Return exercise for update
            MGExerciseModel(exerciseToEdit!!.id, exerciseName, description.text.toString(), exerciseToEdit!!.muscleGroupId)
        }
    }
    /** Returns "Y" if ManageExercisePanel is active, "N" otherwise */
    private fun getOnlyForUserParam(): String {
        return if (StateEngine.panelAdapter.getManageExercisesPanel() != null) {
            "Y"
        } else {
            "N"
        }
    }
}