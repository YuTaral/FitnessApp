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
import com.example.fitnessapp.models.SetModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Edit Exercise dialog to hold the logic to edit exercise */
class EditExerciseDialog {

    companion object {
        /** Show the dialog */
        fun showDialog(exercise: ExerciseModel, onSuccessChange: () -> Unit) {
            // Inflate the dialog layout
            val inflater = LayoutInflater.from(Utils.getContext())
            val dialogView = inflater.inflate(R.layout.edit_exercise_dialog, null)

            // Find the views
            val closeIcon = dialogView.findViewById<ImageView>(R.id.dialog_close)
            val name = dialogView.findViewById<EditText>(R.id.exercise_name)
            val setsContainer = dialogView.findViewById<LinearLayout>(R.id.set_items_container)
            val addSetBtn = dialogView.findViewById<Button>(R.id.add_set_btn)
            val saveBtn = dialogView.findViewById<Button>(R.id.save_btn)
            val deleteBtn = dialogView.findViewById<Button>(R.id.delete_btn)

            // Create the dialog
            val dialogBuilder = AlertDialog.Builder(Utils.getContext())
            dialogBuilder.setView(dialogView).setCancelable(false)

            // Populate the data
            name.setText(exercise.name)
            for (set: SetModel in exercise.sets) {
                addSetToContainer(set, setsContainer)
            }

            // Add click listener to add new set
            addSetBtn.setOnClickListener {
                addSetToContainer(SetModel(), setsContainer)
            }

            // Show the dialog
            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            // Add click listener for the Save button
            saveBtn.setOnClickListener {
                // Validate exercise name
                if (name.text.isEmpty()) {
                    Utils.showMessage(R.string.error_msg_enter_exercise_name)
                    Utils.openKeyboardOnInput(name)
                    return@setOnClickListener
                }

                // Validation passed create the updated exercise and execute the callback
                alertDialog.dismiss()
                saveChanges(ExerciseModel(exercise.id, name.text.toString(), getSets(setsContainer)), onSuccessChange)
            }

            // Add click listener for the Delete button
            deleteBtn.setOnClickListener {
                alertDialog.dismiss()
                deleteExercise(exercise.id, onSuccessChange)
            }

            // Add click listener for the close button
            closeIcon.setOnClickListener {
                alertDialog.dismiss()
            }
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

        /** Executes Edit Exercise Dialog button Save clicked to send a request and update the exercise
         * @param exercise - the updated exercise
         * @param onSuccessChange - callback to be executed if changes were successfully
         */
        private fun saveChanges(exercise: ExerciseModel, onSuccessChange: () -> Unit) {
            if (StateEngine.workout != null) {
                val params = mapOf("exercise" to Utils.serializeObject(exercise), "workoutId" to StateEngine.workout!!.id.toString())

                NetworkManager.sendRequest(
                    APIService.instance.updateExercise(params),
                    onSuccessCallback = { response ->
                        Utils.showToast(R.string.exercise_updated)
                        StateEngine.workout = WorkoutModel(response.returnData.first())
                        onSuccessChange()
                        StateEngine.refreshWorkouts = true
                })
            }
        }

        /** Executes Edit Exercise Dialog button Delete clicked to send a request and delete the exercise
         * @param exerciseId - the exercise id
         * @param onSuccessChange - callback to be executed if changes were successfully
         */
        private fun deleteExercise(exerciseId: Long, onSuccessChange: () -> Unit) {
            if (StateEngine.workout != null) {
                NetworkManager.sendRequest(
                    APIService.instance.deleteExercise(exerciseId),
                    onSuccessCallback = { response ->
                        Utils.showToast(R.string.exercise_deleted)
                        StateEngine.workout = WorkoutModel(response.returnData.first())
                        onSuccessChange()
                        StateEngine.refreshWorkouts = true
                })
            }
        }
    }
}