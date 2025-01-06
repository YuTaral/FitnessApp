package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.EditSetRecAdapter
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.models.SetModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.Utils

/** Edit Exercise dialog to implement the logic to edit exercise */
@SuppressLint("InflateParams")
class EditExerciseFromWorkoutDialog(ctx: Context, exerciseModel: ExerciseModel): BaseDialog(ctx) {
    override var layoutId = R.layout.dialog_edit_exercise
    override var dialogTitleId = R.string.edit_exercise_lbl

    private var exercise = exerciseModel
    private var deletingSets = false

    private lateinit var name: EditText
    private lateinit var notes: EditText
    private lateinit var questionMark: ImageView
    private lateinit var weightLbl: TextView
    private lateinit var chbCompleteAll: CheckBox
    private lateinit var setsRecycler: RecyclerView
    private lateinit var removeSet: ImageView
    private lateinit var addSetBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var deleteBtn: Button

    override fun findViews() {
        super.findViews()

        name = dialog.findViewById(R.id.exercise_name)
        notes = dialog.findViewById(R.id.notes)
        questionMark = dialog.findViewById(R.id.question_mark)
        weightLbl = dialog.findViewById(R.id.weight_lbl)
        setsRecycler = dialog.findViewById(R.id.sets_recycler)
        chbCompleteAll = dialog.findViewById(R.id.complete_all_sets)
        removeSet = dialog.findViewById(R.id.remove_set)
        addSetBtn = dialog.findViewById(R.id.add_set_btn)
        saveBtn = dialog.findViewById(R.id.save_btn)
        deleteBtn = dialog.findViewById(R.id.delete_btn)
    }

    override fun populateDialog() {
        name.setText(exercise.name)

        notes.setText(exercise.notes)

        if (exercise.mGExerciseId == null) {
            questionMark.visibility = View.GONE
        }

        weightLbl.text = String.format(Utils.getActivity().getString(R.string.weight_in_unit_lbl),
                                    AppStateManager.user!!.defaultValues.weightUnit.text)

        // Make suer all sets are initially set to not deletable
        exercise.sets.map { it.deletable = false }

        // Pass copy of exercise.sets, not mutable reference, we don't want to apply unsaved changes
        setsRecycler.adapter = EditSetRecAdapter(ArrayList(exercise.sets))

        // Set the checked state of the Complete all checkbox
        if (exercise.sets.isEmpty()) {
            chbCompleteAll.setChecked(false)
        } else {
            chbCompleteAll.setChecked(exercise.sets.count() == exercise.sets.count { it.completed })
        }
    }

    override fun addClickListeners() {
        super.addClickListeners()

        addSetBtn.setOnClickListener {
            addSet()
        }

        if (exercise.mGExerciseId != null) {
            questionMark.setOnClickListener {
                showExerciseDescription()
            }
        }

        removeSet.setOnClickListener { removeSet() }
        chbCompleteAll.setOnClickListener { getAdapter().changeCompletedState(chbCompleteAll.isChecked) }
        saveBtn.setOnClickListener { save() }
        deleteBtn.setOnClickListener { delete() }
    }

    /** Execute Edit Exercise Dialog button Delete clicked to send a request and delete the exercise */
    private fun delete() {
        val dialog = AskQuestionDialog(Utils.getActivity(), AskQuestionDialog.Question.DELETE_EXERCISE_FROM_WORKOUT, exercise)

        dialog.setLeftButtonCallback {
            if (AppStateManager.workout != null) {
                ExerciseRepository().deleteExerciseFromWorkout(exercise.id, onSuccess = { workout ->
                    dialog.dismiss()
                    dismiss()
                    Utils.getPanelAdapter().refreshWorkoutPanel(workout, true)
                })
            }
        }

        dialog.show()
    }

    /** Send a request to fetch exercise description and show it */
    private fun showExerciseDescription() {
        ExerciseRepository().getMGExercise(exercise.mGExerciseId!!, onSuccess = { mgExerciseModel ->
            DisplayMGExerciseDialog(Utils.getActivity(), exercise.name, mgExerciseModel).show()
        })
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
        val exerciseModel = ExerciseModel(exercise.id, name.text.toString(), MuscleGroupModel(),
                    getAdapter().getSetsData(), exercise.mGExerciseId, notes.text.toString())

        ExerciseRepository().updateExerciseFromWorkout(exerciseModel, onSuccess = { workout ->
                dismiss()
                Utils.getPanelAdapter().refreshWorkoutPanel(workout, true)}
        )
    }

    /** Executed on Add set button click and create new set */
    private fun addSet() {
        // Create set with the default values
        val set = SetModel(0, AppStateManager.user!!.defaultValues.reps,
            AppStateManager.user!!.defaultValues.weight,
            AppStateManager.user!!.defaultValues.rest,
            AppStateManager.user!!.defaultValues.completed)

        getAdapter().addSet(set)

        // Auto scroll to the bottom
        setsRecycler.scrollToPosition(getAdapter().itemCount - 1)
    }

    /** Executed on remove set button click to mark all sets as deletable and let user select
     * which ones must be removed */
    private fun removeSet() {
        deletingSets = !deletingSets

        if (deletingSets) {
            removeSet.setBackgroundResource(R.drawable.icon_delete_set)
        } else {
            removeSet.setBackgroundResource(R.drawable.icon_delete_set_inactive)
        }

        getAdapter().markAllDeletable(deletingSets)
    }

    /** Return teh sets adapter as EditSetRecAdapter */
    private fun getAdapter(): EditSetRecAdapter {
        return  (setsRecycler.adapter as EditSetRecAdapter)
    }
}