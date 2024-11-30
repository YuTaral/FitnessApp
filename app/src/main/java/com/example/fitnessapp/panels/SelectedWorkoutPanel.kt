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
    private lateinit var currentWorkout: TextView
    private lateinit var currentWorkoutDate: TextView
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
        currentWorkout = panel.findViewById(R.id.current_workout_label)
        currentWorkoutDate = panel.findViewById(R.id.current_workout_date_label)
        exerciseRecycler = panel.findViewById(R.id.exercises_recycler)
        newExerciseBtn = panel.findViewById(R.id.add_exercise_btn)
        editBtn = panel.findViewById(R.id.edit_btn)
    }

    override fun populatePanel() {
        if (StateEngine.workout != null) {
            mainContent.visibility = View.VISIBLE
            noWorkoutContent.visibility = View.GONE

            currentWorkout.text = StateEngine.workout!!.name
            currentWorkoutDate.text = Utils.defaultFormatDate(StateEngine.workout!!.date)

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
            StateEngine.panelAdapter
                .displayTemporaryPanel(ExercisePanel(BaseExercisePanel.Mode.SELECT_MUSCLE_GROUP))
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