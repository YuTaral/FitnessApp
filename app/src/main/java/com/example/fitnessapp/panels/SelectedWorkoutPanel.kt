package com.example.fitnessapp.panels

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.ExerciseRecyclerAdapter
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** New Workout Panel class to display and manage the selected workout */
class SelectedWorkoutPanel : PanelFragment() {
    override var id: Long = Constants.PanelUniqueId.WORKOUT.ordinal.toLong()
    override var layoutId: Int = R.layout.selected_workout_panel
    override var panelIndex: Int = 1
    override var titleId: Int = R.string.workout_panel_title

    private lateinit var mainContent: ConstraintLayout
    private lateinit var noWorkoutContent: ConstraintLayout
    private lateinit var workoutName: TextView
    private lateinit var workoutDate: TextView
    private lateinit var workoutStatus: TextView
    private lateinit var exerciseRecycler: RecyclerView
    private lateinit var newExerciseBtn: Button
    private lateinit var editBtn: Button

    override fun onResume() {
        super.onResume()
        populatePanel()
    }

    override fun findViews() {
        mainContent = panel.findViewById(R.id.main_content)
        noWorkoutContent = panel.findViewById(R.id.no_workout_content)
        workoutName = panel.findViewById(R.id.current_workout_label)
        workoutStatus = panel.findViewById(R.id.current_workout_status_value_lbl)
        workoutDate = panel.findViewById(R.id.current_workout_date_label)
        exerciseRecycler = panel.findViewById(R.id.exercises_recycler)
        newExerciseBtn = panel.findViewById(R.id.add_exercise_btn)
        editBtn = panel.findViewById(R.id.edit_btn)
    }

    override fun populatePanel() {
        val statusStringId: Int
        val statusColorId: Int

        if (StateEngine.workout != null) {
            mainContent.visibility = View.VISIBLE
            noWorkoutContent.visibility = View.GONE

            workoutName.text = StateEngine.workout!!.name
            workoutDate.text = Utils.defaultFormatDate(StateEngine.workout!!.startDateTime!!)

             if (StateEngine.workout!!.finishDateTime == null) {
                 statusStringId = R.string.in_progress_lbl
                 statusColorId = R.color.orange
            } else {
                 statusStringId = R.string.finished_lbl
                 statusColorId = R.color.green
            }

            workoutStatus.text = Utils.getContext().getString(statusStringId)
            workoutStatus.setTextColor(requireContext().getColor(statusColorId))

        } else {
            mainContent.visibility = View.GONE
            noWorkoutContent.visibility = View.VISIBLE
        }

        if (exerciseRecycler.adapter == null) {
            exerciseRecycler.layoutManager = LinearLayoutManager(context)
            exerciseRecycler.adapter = ExerciseRecyclerAdapter(listOf())
        }

        if (StateEngine.workout != null) {
            getExercisesRecyclerAdapter().updateData(StateEngine.workout!!.exercises)
        }

        // Set buttons visibility
        setButtonsVisibility()
    }

    override fun addClickListeners() {
        newExerciseBtn.setOnClickListener {
            if (StateEngine.workout!!.finishDateTime != null) {
                // Workout finished, ask the user whether to remove the finished time
                val dialog = AskQuestionDialog(requireContext(), AskQuestionDialog.Question.WORKOUT_ALREADY_FINISHED)

                dialog.setYesCallback {
                    val unFinishedWorkout = StateEngine.workout!!
                    unFinishedWorkout.finishDateTime = null

                    WorkoutRepository().editWorkout(unFinishedWorkout, onSuccess = { workout ->
                        // Refresh the workout panel
                        StateEngine.panelAdapter.displayWorkoutPanel(workout, true)

                        // Close the ask question dialog
                        dialog.dismiss()

                        // Display the add exercise panel after user confirmation and workout marked
                        // as unfinished
                        StateEngine.panelAdapter
                            .displayTemporaryPanel(ExercisePanel(BaseExercisePanel.Mode.SELECT_MUSCLE_GROUP))
                    })
                }

                dialog.show()

            } else {
                // Workout not finished, display the exercise panel
                StateEngine.panelAdapter
                    .displayTemporaryPanel(ExercisePanel(BaseExercisePanel.Mode.SELECT_MUSCLE_GROUP))
            }
        }

        editBtn.setOnClickListener {
            AddEditWorkoutDialog(requireContext(), AddEditWorkoutDialog.Mode.EDIT).show()
        }

        noWorkoutContent.setOnClickListener {
            AddEditWorkoutDialog(requireContext(), AddEditWorkoutDialog.Mode.ADD).show()
        }
    }

    /** Return the exercises recycler adapter */
    private fun getExercisesRecyclerAdapter(): ExerciseRecyclerAdapter {
        return exerciseRecycler.adapter as ExerciseRecyclerAdapter
    }

    /** Sets buttons visibility based on whether workout is selected */
    private fun setButtonsVisibility() {
        if (StateEngine.workout == null) {
            newExerciseBtn.visibility = View.GONE
            editBtn.visibility = View.GONE
        } else {
            newExerciseBtn.visibility = View.VISIBLE
            editBtn.visibility = View.VISIBLE
        }
    }
}