package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.models.SetModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Edit Exercise dialog to hold the logic to edit exercise */
@SuppressLint("InflateParams")
class EditExerciseFromWorkoutDialog(exerciseModel: ExerciseModel) {
    private var exercise: ExerciseModel
    private var dialogView: View
    private var closeIcon: ImageView
    private var name: EditText
    private var setsContainer: LinearLayout
    private var addSetBtn: Button
    private var saveBtn: Button
    private var deleteBtn: Button

    /** Dialog initialization */
    init {
        exercise = exerciseModel

        // Inflate the dialog layout
        dialogView = LayoutInflater.from(Utils.getContext()).inflate(R.layout.edit_exercise_dialog, null)

        // Find the views
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        name = dialogView.findViewById(R.id.exercise_name)
        setsContainer = dialogView.findViewById(R.id.set_items_container)
        addSetBtn = dialogView.findViewById(R.id.add_set_btn)
        saveBtn = dialogView.findViewById(R.id.save_btn)
        deleteBtn = dialogView.findViewById(R.id.delete_btn)
    }

    /** Show the dialog */
    fun showDialog() {
        // Create the dialog
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
        dialogBuilder.setView(dialogView).setCancelable(false)
        val alertDialog = dialogBuilder.create()

        // Populate the data
        name.setText(exercise.name)
        exercise.sets.map { addSetToContainer(it, setsContainer) }

        // Add click listener to add new set
        addSetBtn.setOnClickListener {
            addSetToContainer(SetModel(), setsContainer)
        }

        // Add button click listeners
        saveBtn.setOnClickListener { save(alertDialog) }
        deleteBtn.setOnClickListener { delete(alertDialog) }
        closeIcon.setOnClickListener { alertDialog.dismiss() }

        // Show the dialog
        alertDialog.show()
    }

    /** Populates a single set item and adds it to the container
     * @param set the set to add
     * @param setsContainer the sets container
     */
    @SuppressLint("InflateParams", "SetTextI18n")
    private fun addSetToContainer(set: SetModel, setsContainer: LinearLayout) {
        val inflatableView: View = LayoutInflater.from(Utils.getContext())
            .inflate(R.layout.inflatable_edit_set, null)

        inflatableView.findViewById<CheckBox>(R.id.completed).isChecked = set.completed

        if (set.reps > 0) {
            inflatableView.findViewById<EditText>(R.id.reps_number_txt)
                .setText(set.reps.toString())
        }

        if (set.weight > 0) {
            inflatableView.findViewById<EditText>(R.id.weight_txt)
                .setText(String.format("%.3f", set.weight))
        }

        // Add listener to the remove icon
        inflatableView.findViewById<ImageView>(R.id.remove_set).setOnClickListener {
            setsContainer.removeView(inflatableView)
        }

        // Save the id as tag
        inflatableView.tag = set.id

        setsContainer.addView(inflatableView)
    }

    /** Returns an list of sets
     * @param setsContainer the sets container
     */
    private fun getSets(setsContainer: LinearLayout): MutableList<SetModel>  {
        val sets: MutableList<SetModel> = mutableListOf()

        for (i in 0..<setsContainer.childCount) {
            val currentChild = setsContainer.getChildAt(i)
            val id = currentChild.tag.toString().toLong()
            val completed = currentChild.findViewById<CheckBox>(R.id.completed).isChecked
            val repsVal = currentChild.findViewById<EditText>(R.id.reps_number_txt).text.toString()
            val weightVal = currentChild.findViewById<EditText>(R.id.weight_txt).text.toString()
            var reps = 0
            var weight = 0.0

            if (repsVal.isNotEmpty()) {
                reps = repsVal.toInt()
            }

            if (weightVal.isNotEmpty()) {
                weight = weightVal.toDouble()
            }

            sets.add(SetModel(id, reps, weight, completed))
        }

        return sets
    }

    /** Executes Edit Exercise Dialog button Delete clicked to send a request and delete the exercise
     * @param alertDialog the alert dialog
     * */
    private fun delete(alertDialog: AlertDialog) {
        if (StateEngine.workout != null) {
            ExerciseRepository().deleteExerciseFromWorkout(exercise.id, onSuccess = { workout ->
                alertDialog.dismiss()
                StateEngine.panelAdapter.displayWorkoutPanel(workout, true)
            })
        }
    }

    /** Executed on Save button click
     * @param alertDialog the alert dialog
     */
    private fun save(alertDialog: AlertDialog){
        // Validate exercise name
        if (name.text.isEmpty()) {
            Utils.showMessage(R.string.error_msg_enter_exercise_name)
            Utils.openKeyboardOnInput(name)
            return
        }


        // Edit the exercise, passing empty MuscleGroup object, it is not needed server side
        ExerciseRepository().editExerciseFromWorkout(ExerciseModel(exercise.id, name.text.toString(), MuscleGroupModel(), getSets(setsContainer)),
            onSuccess = { workout ->
                alertDialog.dismiss()
                StateEngine.panelAdapter.displayWorkoutPanel(workout, true)
        })
    }
}