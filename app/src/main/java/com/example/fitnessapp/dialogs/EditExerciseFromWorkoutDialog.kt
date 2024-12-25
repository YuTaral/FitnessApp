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
import android.widget.ScrollView
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.models.SetModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Utils

/** Edit Exercise dialog to hold the logic to edit exercise */
@SuppressLint("InflateParams")
class EditExerciseFromWorkoutDialog(ctx: Context, exerciseModel: ExerciseModel): BaseDialog(ctx) {
    override var layoutId = R.layout.edit_exercise_dialog
    override var dialogTitleId = R.string.edit_exercise_lbl

    private var exercise = exerciseModel
    private var allCompleted: Boolean = true

    private lateinit var name: EditText
    private lateinit var questionMark: ImageView
    private lateinit var weightLbl: TextView
    private lateinit var setsScroller: ScrollView
    private lateinit var setsContainer: LinearLayout
    private lateinit var chbCompleteAll: CheckBox
    private lateinit var addSetBtn: Button
    private lateinit var saveBtn: Button
    private lateinit var deleteBtn: Button

    override fun findViews() {
        super.findViews()

        name = dialog.findViewById(R.id.exercise_name)
        questionMark = dialog.findViewById(R.id.question_mark)
        weightLbl = dialog.findViewById(R.id.weight_lbl)
        setsScroller = dialog.findViewById(R.id.sets_scroller)
        setsContainer = dialog.findViewById(R.id.set_items_container)
        chbCompleteAll = dialog.findViewById(R.id.complete_all_sets)
        addSetBtn = dialog.findViewById(R.id.add_set_btn)
        saveBtn = dialog.findViewById(R.id.save_btn)
        deleteBtn = dialog.findViewById(R.id.delete_btn)
    }

    override fun populateDialog() {
        name.setText(exercise.name)

        if (exercise.mGExerciseId == null) {
            questionMark.visibility = View.GONE
        }

        weightLbl.text = String.format(Utils.getActivity().getString(R.string.weight_in_unit_lbl),
                                    AppStateManager.user!!.defaultValues.weightUnit.text)

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
        super.addClickListeners()

        addSetBtn.setOnClickListener {
            // Create set with the default values
            val set = SetModel(0, AppStateManager.user!!.defaultValues.reps,
                                        AppStateManager.user!!.defaultValues.weight,
                                        AppStateManager.user!!.defaultValues.completed)

            addSetToContainer(set, setsContainer, false)

            // Auto scroll to the bottom of the sets container so the newly added set is visible
            setsScroller.post {
                setsScroller.fullScroll(View.FOCUS_DOWN)
            }
        }

        if (exercise.mGExerciseId != null) {
            questionMark.setOnClickListener {
                showExerciseDescription()
            }
        }

        chbCompleteAll.setOnClickListener { changeSetsCompletedState() }
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

        val inflatableView: View = LayoutInflater.from(Utils.getActivity())
            .inflate(R.layout.inflatable_edit_set, null)

        inflatableView.findViewById<CheckBox>(R.id.completed).isChecked = set.completed

        if (set.reps > 0) {
            inflatableView.findViewById<EditText>(R.id.reps_number_txt)
                .setText(set.reps.toString())
        }

        if (set.weight > 0) {
            inflatableView.findViewById<EditText>(R.id.weight_txt).setText(Utils.formatDouble(set.weight))
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
            getSets(setsContainer), exercise.mGExerciseId)

        ExerciseRepository().updateExerciseFromWorkout(exerciseModel, onSuccess = { workout ->
                dismiss()
                Utils.getPanelAdapter().refreshWorkoutPanel(workout, true)}
        )
    }


    /** Change all sets completed state based on whether Complete all sets checkbox is checked */
    private fun changeSetsCompletedState() {
        for (i in 0..<setsContainer.childCount) {
            setsContainer.getChildAt(i).findViewById<CheckBox>(R.id.completed)
                .setChecked(chbCompleteAll.isChecked)
        }
    }
}