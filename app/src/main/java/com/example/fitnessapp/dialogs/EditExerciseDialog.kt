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
import com.example.fitnessapp.utils.Utils

/** Edit Exercise dialog to hold the logic to edit exercise */
class EditExerciseDialog {

    companion object {
        /** Show the dialog
         * @param onSave callback to execute on Save button click
         */
        fun showDialog(exercise: ExerciseModel, onSave: (ExerciseModel) -> Unit) {
            // Inflate the dialog layout
            val inflater = LayoutInflater.from(Utils.getActivity())
            val dialogView = inflater.inflate(R.layout.edit_exercise_dialog, null)

            // Find the views
            val closeIcon = dialogView.findViewById<ImageView>(R.id.dialog_close)
            val name = dialogView.findViewById<EditText>(R.id.exercise_name)
            val setsContainer = dialogView.findViewById<LinearLayout>(R.id.set_items_container)
            val addSetBtn = dialogView.findViewById<Button>(R.id.add_set_btn)
            val saveBtn = dialogView.findViewById<Button>(R.id.save_btn)

            // Create the dialog
            val dialogBuilder = AlertDialog.Builder(Utils.getActivity())
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

            saveBtn.setOnClickListener {
                // Validate exercise name
                if (name.text.isEmpty()) {
                    Utils.showMessage(R.string.error_msg_enter_exercise_name)
                    Utils.openKeyboardOnInput(name)
                    return@setOnClickListener
                }

                // Validation passed create the updated exercise and execute the callback
                alertDialog.dismiss()
                onSave(ExerciseModel(exercise.id, name.text.toString(), getSets(setsContainer)))
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
            val inflatableView: View = LayoutInflater.from(Utils.getActivity())
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
    }
}