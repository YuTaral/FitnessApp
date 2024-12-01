package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.CustomResponse
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Add / Edit Muscle group Exercise dialog to hold the logic for
 * adding exercise / updating exercise as part of muscle group
 */
@SuppressLint("InflateParams")
class AddEditMGExerciseDialog(ctx: Context, mGroupId: Long, exercise: MGExerciseModel? = null): BaseDialog(ctx) {
    override var layoutId = R.layout.add_edit_mgexercise_dialog

    private var muscleGroupId = mGroupId
    private var exerciseToEdit = exercise

    private lateinit var title: TextView
    private lateinit var name: EditText
    private lateinit var description: EditText
    private lateinit var addExerciseToWorkout: CheckBox
    private lateinit var saveBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Open the keyboard once the dialog is shown
        if (exerciseToEdit == null) {
            name.requestFocus()
            window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    override fun findViews() {
        title = dialogView.findViewById(R.id.add_exercise_dialog_title)
        name = dialogView.findViewById(R.id.exercise_name)
        description = dialogView.findViewById(R.id.exercise_description)
        addExerciseToWorkout = dialogView.findViewById(R.id.add_exercise_to_workout)
        saveBtn = dialogView.findViewById(R.id.save_btn)
    }

    override fun populateDialog() {
        // Change the title and populate the data if edit
        if (exerciseToEdit != null) {
            title.setText(R.string.edit_exercise_lbl)
            name.setText(exerciseToEdit!!.name)
            description.setText(exerciseToEdit!!.description)
            addExerciseToWorkout.visibility = View.GONE
        }

        if (StateEngine.panelAdapter.isManageExerciseActive()) {
            // Remove the add exercise to workout checkbox if we are
            // managing the exercises
            addExerciseToWorkout.visibility = View.GONE
        }
    }

    override fun addClickListeners() {
        saveBtn.setOnClickListener { save("Y") }
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
                dismiss()

                // Response contains the updated muscle group exercises
                StateEngine.panelAdapter.getBaseExercisePanel()!!
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
            StateEngine.panelAdapter.getBaseExercisePanel()!!
                .populateExercises(returnData.map { MGExerciseModel(it) }, false)

        }
        dismiss()
    }

    /** Callback to execute when Add / Edit request failed
     * @param response the response returned by the server
     */
    private fun onFailureCallback(response: CustomResponse) {
        if (response.responseCode == Constants.ResponseCode.EXERCISE_ALREADY_EXISTS.ordinal) {
            // Display dialog asking the user whether to override the existing exercise
            val dialog = AskQuestionDialog(Utils.getContext(),
                                            AskQuestionDialog.Question.OVERRIDE_EXISTING_EXERCISE,
                                            MGExerciseModel(response.responseData[0]))

            dialog.setYesCallback {
                val existingExercise = MGExerciseModel(response.responseData[0])
                existingExercise.description = description.text.toString()

                ExerciseRepository().updateExercise(existingExercise, getOnlyForUserParam(), onSuccess = { returnData ->
                    // Close the dialogs
                    dialog.dismiss()
                    dismiss()

                    // Updated the exercises
                    StateEngine.panelAdapter.getBaseExercisePanel()!!
                        .populateExercises(returnData.map { MGExerciseModel(it) }, false)
                })
            }

            dialog.setNoCallback {
                dialog.dismiss()
                save("N")
            }

            dialog.show()
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
        return if (StateEngine.panelAdapter.isManageExerciseActive()) {
            "Y"
        } else {
            "N"
        }
    }
}