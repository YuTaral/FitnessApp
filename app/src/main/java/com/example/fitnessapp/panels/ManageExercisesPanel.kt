package com.example.fitnessapp.panels

import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.CustomSpinnerAdapter
import com.example.fitnessapp.adapters.MGExercisesRecyclerAdapter
import com.example.fitnessapp.adapters.MuscleGroupRecyclerAdapter
import com.example.fitnessapp.dialogs.AddEditMGExerciseDialog
import com.example.fitnessapp.dialogs.DialogAskQuestion
import com.example.fitnessapp.interfaces.IExercisePanel
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.network.repositories.MuscleGroupRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for Manage Exercises Panel */
class ManageExercisesPanel(mode: Mode): PanelFragment(), IExercisePanel {
    override var id: Long = Constants.PanelUniqueId.MANAGE_EXERCISE.ordinal.toLong()
    override var layoutId: Int = R.layout.manage_exercises_panel
    override var panelIndex: Int = 2
    override var titleId: Int = R.string.manage_exercises_lbl

    /** Enum to hold the different states of the panel */
    enum class Mode {
        SELECT_MUSCLE_GROUP,
        EDIT_EXERCISE
    }

    private lateinit var actionSpinner: Spinner
    private lateinit var title: TextView
    private lateinit var muscleGroupRecycler: RecyclerView
    private lateinit var exercisesRecycler: RecyclerView
    private lateinit var buttonsContainer: ConstraintLayout
    private lateinit var backBtn: Button
    private lateinit var addBtn: Button
    private lateinit var muscleGroups: MutableList<MuscleGroupModel>
    private var actionSpinnerEditExIndex: Int = 1
    private var selectedMuscleGroupId: Long = 0
    private var panelMode: Mode = mode

    override fun initializePanel() {
        muscleGroups = mutableListOf()

        // Find the views
        actionSpinner = panel.findViewById(R.id.exercise_action_spinner)
        title = panel.findViewById(R.id.exercise_panel_title)
        muscleGroupRecycler = panel.findViewById(R.id.muscle_groups_recycler)
        exercisesRecycler = panel.findViewById(R.id.exercises_recycler)
        buttonsContainer = panel.findViewById(R.id.buttons_container)
        backBtn = panel.findViewById(R.id.back_btn)
        addBtn = panel.findViewById(R.id.add_btn)

        // Populate the panel
        populatePanel()

        // Click listeners
        backBtn.setOnClickListener {
            // Update the mode, reset the selectedMuscleGroupId and update the views
            panelMode = Mode.SELECT_MUSCLE_GROUP
            selectedMuscleGroupId = 0
            updateViews()
        }
        addBtn.setOnClickListener { AddEditMGExerciseDialog(selectedMuscleGroupId).showDialog() }
    }

    /** Populates the data in the panel */
    private fun populatePanel() {
        updateViews()

        if (muscleGroups.isEmpty()) {
            // Fetch the muscle groups if the list is empty
            MuscleGroupRepository().getMuscleGroups(onSuccess = { data ->
                muscleGroups = data

                muscleGroupRecycler.layoutManager = LinearLayoutManager(context)
                muscleGroupRecycler.adapter = MuscleGroupRecyclerAdapter(muscleGroups,
                    callback = { muscleGroupId ->
                        selectedMuscleGroupId = muscleGroupId

                        ExerciseRepository().getMuscleGroupExercises(muscleGroupId,"Y", onSuccess = { exercises ->
                            populateExercises(exercises, true)
                        })
                    })
            })
        }
    }

    /** Populates the exercises of the given muscle group
     * @param exercises the exercises to display
     * @param initializeAdapter true if the adapter should be initialized, false if we just want
     * to update the data
     */
    override fun populateExercises(exercises: List<MGExerciseModel>, initializeAdapter: Boolean) {
        // Set the mode
        panelMode = Mode.EDIT_EXERCISE

        // Update the views after the mode has been set
        updateViews()

        if (exercises.isEmpty()) {
            // Show no exercises
            showViewsWhenNoExercises(initializeAdapter)
        } else {
            // Display the exercises
            displayExercises(exercises, initializeAdapter)
        }
    }

    /** Changes the visibility the views in the panel to display the muscle groups or exercises */
    private fun updateViews() {
        when (panelMode) {
            Mode.SELECT_MUSCLE_GROUP -> {
                actionSpinner.visibility = View.GONE
                title.visibility = View.VISIBLE
                title.text = requireContext().getString(R.string.select_muscle_group_lbl)
                muscleGroupRecycler.visibility = View.VISIBLE
                exercisesRecycler.visibility = View.GONE
                buttonsContainer.visibility = View.GONE
            }
            else -> {
                title.visibility = View.GONE
                actionSpinner.visibility = View.VISIBLE
                muscleGroupRecycler.visibility = View.GONE
                exercisesRecycler.visibility = View.VISIBLE
                buttonsContainer.visibility = View.VISIBLE

                // Create the adapter
                if (actionSpinner.adapter == null) {
                    actionSpinner.adapter = CustomSpinnerAdapter(panel.context, true, listOf(
                        requireContext().getString(R.string.select_action_lbl),
                        requireContext().getString(R.string.action_update_exercise),
                        requireContext().getString(R.string.action_delete_exercise))
                    )
                }
            }
        }
    }

    /** Executes the logic to update views there are exercises for the selected muscle group
     * @param exercises the exercises to display
     * @param initializeAdapter true if the recycler adapter needs initialization, false otherwise
     */
    private fun displayExercises(exercises: List<MGExerciseModel>, initializeAdapter: Boolean) {
        if (initializeAdapter) {
            exercisesRecycler.layoutManager = LinearLayoutManager(context)

            exercisesRecycler.adapter = MGExercisesRecyclerAdapter(exercises, callback = { model ->
                // Set the on select callback
                onClickCallback(model)
            })

        } else {
            (exercisesRecycler.adapter as MGExercisesRecyclerAdapter).updateData(exercises)
        }
    }

    /** Executes the logic on exercise click depending on the mode
     * @param model the exercise that was clicked
     */
    private fun onClickCallback(model: MGExerciseModel) {
        // Set the on select callback
        if (actionSpinner.selectedItemPosition == 0) {
            Utils.showToast(R.string.error_select_exercise_action)
            return
        }

        if (actionSpinner.selectedItemPosition == actionSpinnerEditExIndex) {
            // Edit exercise
            AddEditMGExerciseDialog(selectedMuscleGroupId, model).showDialog()

        } else {
            // Delete exercise
            val dialog = DialogAskQuestion(DialogAskQuestion.Question.DELETE_MG_EXERCISE)

            dialog.setConfirmCallback {
                ExerciseRepository().deleteExercise(model.id, onSuccess = { mGExercises ->
                    // Re-populate on success
                    dialog.closeDialog()
                    populateExercises(mGExercises, false)
                })
            }

            dialog.showDialog(model)
        }
    }

    /** Executes the logic to update views when no exercises has been found for the selected muscle group
     * @param initializeAdapter true if the recycler adapter needs initialization, false otherwise
     */
    private fun showViewsWhenNoExercises(initializeAdapter: Boolean) {
        actionSpinner.visibility = View.GONE
        title.visibility = View.VISIBLE

        // Show title that there is no exercises for this muscle group
        title.text = requireContext().getString(R.string.no_exercise_to_edit_lbl)

        if (initializeAdapter) {
            exercisesRecycler.layoutManager = LinearLayoutManager(context)
            exercisesRecycler.adapter = MGExercisesRecyclerAdapter(mutableListOf(), callback = {})
        } else {
            (exercisesRecycler.adapter as MGExercisesRecyclerAdapter).updateData(mutableListOf())
        }
    }
}
