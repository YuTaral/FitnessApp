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
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Add / Edit Workout dialog to hold the logic for add / edit workout */
class AddEditWorkoutDialog {
    companion object {

        /** Show the dialog
         * @param add true if dialog mode is add (adding workout), false if editing exercise
         * @param onSave callback to execute on Save button click
         */
        @SuppressLint("InflateParams")
        fun showDialog(add: Boolean, onSave: (WorkoutModel) -> Unit) {
            // Inflate the dialog layout
            val inflater = LayoutInflater.from(Utils.getContext())
            val dialogView = inflater.inflate(R.layout.add_edit_workout_dialog, null)

            // Find the views
            val title = dialogView.findViewById<TextView>(R.id.add_workout_title)
            val closeIcon = dialogView.findViewById<ImageView>(R.id.dialog_close)
            val name = dialogView.findViewById<EditText>(R.id.workout_name_txt)
            val muscleGroupsRecycler = dialogView.findViewById<RecyclerView>(R.id.muscle_groups_recycler)
            val saveBtn = dialogView.findViewById<Button>(R.id.save_btn)
            val deleteBtn = dialogView.findViewById<Button>(R.id.delete_btn)

            // Create the dialog
            val dialogBuilder = AlertDialog.Builder(Utils.getContext())
            dialogBuilder.setView(dialogView).setCancelable(false)

            // Populate the dialog and change the views
            if (add) {
                populateMuscleGroups(muscleGroupsRecycler)
            } else {
                title.text = Utils.getContext().getString(R.string.edit_workout_panel_title)
                deleteBtn.visibility = View.VISIBLE
                populateDialog(name, muscleGroupsRecycler)
            }

            // Show the dialog
            val alertDialog = dialogBuilder.create()
            alertDialog.show()

            // Open the keyboard once the dialog is shown
            name.requestFocus()
            alertDialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            alertDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

            // Set button click listeners
            saveBtn.setOnClickListener {
                var id: Long = 0
                // Validate
                if (name.text.isEmpty()) {
                    Utils.validationFailed(name, R.string.error_msg_enter_workout_name)
                    return@setOnClickListener
                }

                // Set the id if we are editing the workout
                if (!add) {
                    id = StateEngine.workout!!.id
                }

                // Add / Edit the workout
                alertDialog.dismiss()
                onSave(WorkoutModel(id, name.text.toString(), mutableListOf(),
                    (muscleGroupsRecycler.adapter as MuscleGroupRecyclerAdapter).getSelectedMuscleGroups()))
            }

            deleteBtn.setOnClickListener {
                // Send a request to delete the workout
                NetworkManager.sendRequest(APIService.instance.deleteWorkout(StateEngine.workout!!.id),
                    onSuccessCallback = { _ ->
                        Utils.showToast(R.string.workout_deleted)
                        alertDialog.dismiss()
                        StateEngine.workout = null
                        Utils.getActivity().displayNewWorkoutPanel()
                        StateEngine.refreshWorkouts = true
                    }
                )
            }

            closeIcon.setOnClickListener {
                alertDialog.dismiss()
            }
        }

        /** Fetches the Muscle Groups and populates the dialog
         * @param muscleGroupsRecycler the recycler view
         **/
        private fun populateMuscleGroups(muscleGroupsRecycler: RecyclerView) {
            NetworkManager.sendRequest(
                APIService.instance.getMuscleGroups(StateEngine.user.id),
                onSuccessCallback = { response ->
                    val muscleGroups : MutableList<MuscleGroupModel> = mutableListOf()

                    for (d : String in response.returnData) {
                        muscleGroups.add(MuscleGroupModel(d))
                    }

                    muscleGroupsRecycler.layoutManager = LinearLayoutManager(Utils.getContext())
                    muscleGroupsRecycler.adapter = MuscleGroupRecyclerAdapter(muscleGroups)
                }
            )
        }

        /** Populates the dialog when mode is edit
         * @param name the name view
         * @param muscleGroupsRecycler the recycler view
         **/
        private fun populateDialog(name: EditText, muscleGroupsRecycler: RecyclerView) {
            if (StateEngine.workout != null) {
                name.setText(StateEngine.workout!!.name)
                muscleGroupsRecycler.layoutManager = LinearLayoutManager(Utils.getContext())
                muscleGroupsRecycler.adapter = MuscleGroupRecyclerAdapter(StateEngine.workout!!.muscleGroups)
            }
        }
    }
}
