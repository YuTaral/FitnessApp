package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.EditSetAdapter
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
    private lateinit var setsScroller: ScrollView
    private lateinit var setsContainer: LinearLayout
    private lateinit var setsAdapter: EditSetAdapter
    private lateinit var removeSetContainer: ConstraintLayout
    private lateinit var removeSet: ImageView
    private lateinit var addSetContainer: ConstraintLayout
    private lateinit var saveBtn: Button
    private lateinit var deleteBtn: Button

    override fun findViews() {
        super.findViews()

        name = dialog.findViewById(R.id.exercise_name)
        notes = dialog.findViewById(R.id.notes)
        questionMark = dialog.findViewById(R.id.question_mark)
        weightLbl = dialog.findViewById(R.id.weight_lbl)
        setsScroller = dialog.findViewById(R.id.sets_scroller)
        setsContainer = dialog.findViewById(R.id.sets_linear_container)
        removeSetContainer = dialog.findViewById(R.id.remove_set_container)
        removeSet = dialog.findViewById(R.id.remove_set)
        addSetContainer = dialog.findViewById(R.id.add_set_container)
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
        setsAdapter = EditSetAdapter(setsContainer, ArrayList(exercise.sets))
    }

    override fun addClickListeners() {
        super.addClickListeners()

        addSetContainer.setOnClickListener {
            addSet()
        }

        if (exercise.mGExerciseId != null) {
            questionMark.setOnClickListener {
                showExerciseDescription()
            }
        }

        removeSetContainer.setOnClickListener { startRemoveSet() }
        saveBtn.setOnClickListener { save() }
        deleteBtn.setOnClickListener { delete() }
    }

    /** Execute Edit Exercise Dialog button Delete clicked to send a request and delete the exercise */
    private fun delete() {
        val dialog = AskQuestionDialog(Utils.getActivity(), AskQuestionDialog.Question.DELETE_EXERCISE_FROM_WORKOUT, exercise)

        dialog.setConfirmButtonCallback {
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
                            setsAdapter.getSetsData(), exercise.mGExerciseId, notes.text.toString())

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
            AppStateManager.user!!.defaultValues.completed,
            deletingSets)

        setsAdapter.addSet(set)

        // Auto scroll to the bottom of the sets container so the newly added set is visible
        setsScroller.postDelayed({
            setsScroller.fullScroll(View.FOCUS_DOWN)
        }, 500)
    }

    /** Executed on remove set button click to mark all sets as deletable and let user select
     * which ones must be removed */
    private fun startRemoveSet() {
        deletingSets = !deletingSets

        if (deletingSets) {
            removeSet.setBackgroundResource(R.drawable.icon_delete_set)
        } else {
            removeSet.setBackgroundResource(R.drawable.icon_delete_set_inactive)
        }

        setsAdapter.markAllDeletable(deletingSets)
    }
}