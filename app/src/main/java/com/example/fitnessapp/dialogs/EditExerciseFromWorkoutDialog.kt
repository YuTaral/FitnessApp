package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.fitnessapp.R
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.models.SetModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Edit Exercise dialog to hold the logic to edit exercise */
@SuppressLint("InflateParams")
class EditExerciseFromWorkoutDialog(ctx: Context, exerciseModel: ExerciseModel): BaseAlertDialog(ctx) {
    override var layoutId = R.layout.edit_exercise_dialog

    private var exercise = exerciseModel
    private var allCompleted: Boolean = true

    private lateinit var name: EditText
    private lateinit var setsContainer: LinearLayout
    private lateinit var chbCompleteAll: CheckBox
    private lateinit var addSetBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var deleteBtn: Button

    override fun findViews() {
        name = dialogView.findViewById(R.id.exercise_name)
        setsContainer = dialogView.findViewById(R.id.set_items_container)
        chbCompleteAll = dialogView.findViewById(R.id.complete_all_sets)
        addSetBtn = dialogView.findViewById(R.id.add_set_btn)
        saveBtn = dialogView.findViewById(R.id.save_btn)
        deleteBtn = dialogView.findViewById(R.id.delete_btn)
    }

    override fun populateDialog() {
        name.setText(exercise.name)
        exercise.sets.map { addSetToContainer(it, setsContainer, true) }

        if (exercise.sets.isEmpty()) {
            allCompleted = false
        }

        if (allCompleted) {
            // Complete all checkbox must be checked by default
            chbCompleteAll.setChecked(true)
        }
    }

    override fun addClickListeners() {
        chbCompleteAll.setOnClickListener { changeSetsCompletedState() }
        addSetBtn.setOnClickListener {
            addSetToContainer(SetModel(), setsContainer, false)
        }
        saveBtn.setOnClickListener { save() }
        deleteBtn.setOnClickListener { delete() }
    }

    /** Populate a single set item and adds it to the container
     * @param set the set to add
     * @param setsContainer the sets container
     * @param checkCompleted true if set.completed must be checked to set allCompleted, false otherwise
     */
    @SuppressLint("InflateParams", "SetTextI18n")
    private fun addSetToContainer(set: SetModel, setsContainer: LinearLayout, checkCompleted: Boolean) {
        if (checkCompleted && !set.completed) {
            allCompleted = false
        }

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

    /** Return list of sets
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

    /** Execute Edit Exercise Dialog button Delete clicked to send a request and delete the exercise */
    private fun delete() {
        if (StateEngine.workout != null) {
            ExerciseRepository().deleteExerciseFromWorkout(exercise.id, onSuccess = { workout ->
                dismiss()
                StateEngine.panelAdapter.displayWorkoutPanel(workout, true)
            })
        }
    }

    /** Executed on Save button click */
    private fun save(){
        // Validate exercise name
        if (name.text.isEmpty()) {
            Utils.showMessage(R.string.error_msg_enter_exercise_name)
            Utils.openKeyboardOnInput(name)
            return
        }

        // Edit the exercise, passing empty MuscleGroup object, it is not needed server side
        ExerciseRepository().editExerciseFromWorkout(ExerciseModel(exercise.id, name.text.toString(), MuscleGroupModel(), getSets(setsContainer)),
            onSuccess = { workout ->
                dismiss()
                StateEngine.panelAdapter.displayWorkoutPanel(workout, true)
        })
    }


    /** Change all sets completed state based on whether Complete all sets checkbox is checked */
    private fun changeSetsCompletedState() {
        for (i in 0..<setsContainer.childCount) {
            setsContainer.getChildAt(i).findViewById<CheckBox>(R.id.completed)
                .setChecked(chbCompleteAll.isChecked)
        }
    }
}