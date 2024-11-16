package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.R
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Add Exercise dialog to hold the logic for adding exercise */
@SuppressLint("InflateParams")
class AddExerciseDialog(mode: Mode, exercise: MGExerciseModel? = null, muscleGroupIdVal: Long? = 0) {
    private var dialogMode: Mode
    private var exerciseToAdd: MGExerciseModel?
    private var muscleGroupId: Long?
    private var dialogView: View
    private lateinit var alertDialog: AlertDialog
    private var closeIcon: ImageView
    private var name: EditText
    private var newExerciseContainer: ConstraintLayout
    private var description: EditText
    private var addExerciseToWorkout: CheckBox
    private var newExerciseToWorkoutContainer: ConstraintLayout
    private var sets: EditText
    private var reps: EditText
    private var weight: EditText
    private var exerciseCompleted: CheckBox
    private var saveBtn: Button

    /** The dialog mode - create new exercise / add exercise to workout */
    enum class Mode {
        CREATE_NEW,
        ADD_TO_WORKOUT
    }

    /** Dialog initialization */
    init {
        dialogMode = mode
        exerciseToAdd = exercise
        muscleGroupId = muscleGroupIdVal

        // Inflate the dialog layout
        dialogView = LayoutInflater.from(Utils.getContext()).inflate(R.layout.add_exercise_dialog, null)

        // Find the views
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        name = dialogView.findViewById(R.id.exercise_name)
        newExerciseContainer = dialogView.findViewById(R.id.new_exercise_container)
        description = dialogView.findViewById(R.id.exercise_description)
        addExerciseToWorkout = dialogView.findViewById(R.id.add_exercise_to_workout)
        newExerciseToWorkoutContainer = dialogView.findViewById(R.id.exercise_to_workout_container)
        sets = dialogView.findViewById(R.id.exercise_sets)
        reps = dialogView.findViewById(R.id.set_reps)
        weight = dialogView.findViewById(R.id.exercise_weight)
        exerciseCompleted = dialogView.findViewById(R.id.complete_exercise)
        saveBtn = dialogView.findViewById(R.id.save_btn)
    }

    /** Show the dialog */
    fun showDialog() {
        // Create the dialog
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
        dialogBuilder.setView(dialogView).setCancelable(false)
        alertDialog = dialogBuilder.create()

        // Add button click listeners
        saveBtn.setOnClickListener { save() }
        closeIcon.setOnClickListener { alertDialog.dismiss() }

        if (dialogMode == Mode.ADD_TO_WORKOUT) {
            // Set the exercise name
            name.setText(exerciseToAdd!!.name)
        } else {
            // Display the exercise description and hide the container
            // with sets, reps and weight
            newExerciseToWorkoutContainer.visibility = View.GONE
            newExerciseContainer.visibility = View.VISIBLE

            val layoutParams = saveBtn.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topToBottom = R.id.new_exercise_container
            saveBtn.layoutParams = layoutParams
        }

        // Show the dialog
        alertDialog.show()

        // Open the keyboard once the dialog is shown
        if (dialogMode == Mode.CREATE_NEW) {
            name.requestFocus()
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

    /** Executed on Save button click */
    private fun save() {
        if (dialogMode == Mode.ADD_TO_WORKOUT) {
            val exercise = validateExercise() ?: return
            ExerciseRepository().addExerciseToWorkout(exercise, onSuccess = { workout ->
                alertDialog.dismiss()
                StateEngine.panelAdapter.displayWorkoutPanel(workout, true)
            })
        } else {
            val exercise = validateNewExercise() ?: return

            // Pass the workout id if we need to add automatically the newly created exercise
            // to the current workout
            val workoutId = if (addExerciseToWorkout.isChecked) {
                StateEngine.workout!!.id.toString()
            } else {
                "0"
            }

            ExerciseRepository().addExercise(exercise, workoutId, onSuccess = { serializedData ->
                alertDialog.dismiss()

                if (workoutId == "0") {
                    // Workout id was not sent and the exercise was not added to the current workout
                    // remain on the Exercises page and refresh to data to display the newly added exercise
                    val exercisePanel = StateEngine.panelAdapter.getExercisePanel()

                    if (exercisePanel != null) {
                        val exercises = serializedData.map { MGExerciseModel(it) }
                        exercisePanel.displayExercises(exercises, false)
                    }

                } else {
                    // Workout id was sent and the exercise was automatically added to the current workout
                    // Update the workout in this case
                    StateEngine.panelAdapter.displayWorkoutPanel(WorkoutModel(serializedData[0]), true)
                }
            })
        }
    }


    /** Validate the data in the dialog when save is clicked and exercise is being added to the workout */
    private fun validateExercise(): ExerciseModel? {
        val exerciseName = name.text.toString()
        var exerciseSets = 0
        var setReps = 0
        var exerciseWeight = 0.0

        if (sets.text.toString().isNotEmpty()) {
            exerciseSets = sets.text.toString().toInt()
        }
        if (reps.text.toString().isNotEmpty()) {
            setReps = reps.text.toString().toInt()
        }
        if (weight.text.toString().isNotEmpty()) {
            exerciseWeight = weight.text.toString().toDouble()
        }

        // Validate Name
        if (exerciseName.isEmpty()) {
            Utils.validationFailed(name, R.string.error_msg_enter_ex_name)
            return null
        }

        // Create MuscleGroup object only with id, that's the variable which is needed
        // server side in order to create Exercise record
        val model = MuscleGroupModel(exerciseToAdd!!.muscleGroupId)

        // Validation passed
        return ExerciseModel(exerciseName, model, exerciseSets, setReps, exerciseWeight, exerciseCompleted.isChecked)
    }

    /** Validate the data in the dialog when save is clicked and new exercise is being added */
    private fun validateNewExercise(): MGExerciseModel? {
        val exerciseName = name.text.toString()

        // Validate Name
        if (exerciseName.isEmpty()) {
            Utils.validationFailed(name, R.string.error_msg_enter_ex_name)
            return null
        }

        // Validation passed
        return MGExerciseModel(exerciseName, description.text.toString(), muscleGroupId!!)
    }
}