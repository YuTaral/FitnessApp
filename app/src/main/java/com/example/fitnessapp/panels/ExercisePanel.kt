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
import com.example.fitnessapp.dialogs.AddExerciseDialog
import com.example.fitnessapp.dialogs.DialogAskQuestion
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.network.repositories.MuscleGroupRepository
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for Exercise Panel */
class ExercisePanel(mode: Mode): PanelFragment()  {

    override var id: Long = 4
    override var layoutId: Int = R.layout.exercise_panel
    override var panelIndex: Int = 2
    override var titleId: Int = R.string.exercise_lbl

    /** Enum to hold the different states of the panel */
    enum class Mode {
        SELECT_MG_BEFORE_SELECT_EXERCISE,
        SELECT_EXERCISE,
        SELECT_MG_BEFORE_EDIT_EXERCISE,
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
            if (panelMode == Mode.SELECT_EXERCISE) {
                panelMode = Mode.SELECT_MG_BEFORE_SELECT_EXERCISE

            } else if (panelMode == Mode.EDIT_EXERCISE) {
                panelMode = Mode.SELECT_MG_BEFORE_EDIT_EXERCISE

            }

            selectedMuscleGroupId = 0
            updateViews()
        }
        addBtn.setOnClickListener {
            AddExerciseDialog(AddExerciseDialog.Mode.CREATE_NEW, null, selectedMuscleGroupId)
                .showDialog()
        }
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
                        var onlyForUser = "N"

                        selectedMuscleGroupId = muscleGroupId

                        if (panelMode == Mode.SELECT_MG_BEFORE_EDIT_EXERCISE ||
                            panelMode == Mode.EDIT_EXERCISE) {

                            onlyForUser = "Y"
                        }

                        ExerciseRepository().getMuscleGroupExercises(muscleGroupId,onlyForUser, onSuccess = { exercises ->
                            displayExercises(exercises, true)
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
    fun displayExercises(exercises: List<MGExerciseModel>, initializeAdapter: Boolean) {
        if (panelMode == Mode.SELECT_MG_BEFORE_SELECT_EXERCISE) {
            panelMode = Mode.SELECT_EXERCISE

        } else if (panelMode == Mode.SELECT_MG_BEFORE_EDIT_EXERCISE) {
            panelMode = Mode.EDIT_EXERCISE
        }

        updateViews()

        exercisesRecycler.layoutManager = LinearLayoutManager(context)

        if (exercises.isEmpty()) {
            if (panelMode == Mode.EDIT_EXERCISE) {
                actionSpinner.visibility = View.GONE
                title.visibility = View.VISIBLE
            }

            // Show title that there is no exercises for this muscle group
            title.text = requireContext().getString(R.string.no_exercise_lbl)

            if (initializeAdapter) {
                exercisesRecycler.adapter = MGExercisesRecyclerAdapter(mutableListOf(), callback = {})
            } else {
                (exercisesRecycler.adapter as MGExercisesRecyclerAdapter).updateData(mutableListOf())
            }
        } else {
            // Display the exercises
            if (initializeAdapter) {
                exercisesRecycler.adapter = MGExercisesRecyclerAdapter(exercises, callback = { model ->

                    // Set the on select callback
                    if (panelMode == Mode.SELECT_EXERCISE) {
                        // Add exercise to workout
                        AddExerciseDialog(AddExerciseDialog.Mode.ADD_TO_WORKOUT, model).showDialog()

                    } else if (panelMode == Mode.EDIT_EXERCISE) {

                        if (actionSpinner.selectedItemPosition == 0) {
                            Utils.showToast(R.string.error_select_exercise_action)
                            return@MGExercisesRecyclerAdapter
                        }

                        if (actionSpinner.selectedItemPosition == actionSpinnerEditExIndex) {
                            // Edit exercise
                            //TODO: Update the muscle group exercise
                            Utils.showMessage("UPDATING EXERCISE " + model.name)

                        } else {
                            // Delete exercise
                            val dialog = DialogAskQuestion(DialogAskQuestion.Question.DELETE_MG_EXERCISE)

                            dialog.setConfirmCallback {
                                ExerciseRepository().deleteExercise(model.id, onSuccess = { mGExercises ->
                                    // Re-populate on success
                                    dialog.closeDialog()
                                    displayExercises(mGExercises, false)
                                })
                            }

                            dialog.showDialog(model)
                        }
                    }
                })

            } else {
                (exercisesRecycler.adapter as MGExercisesRecyclerAdapter).updateData(exercises)
            }
        }
    }

    /** Changes the visibility the views in the panel to display the muscle groups or exercises */
    private fun updateViews() {
        when (panelMode) {
            Mode.SELECT_MG_BEFORE_SELECT_EXERCISE,
            Mode.SELECT_MG_BEFORE_EDIT_EXERCISE -> {
                actionSpinner.visibility = View.GONE
                title.visibility = View.VISIBLE
                title.text = requireContext().getString(R.string.select_muscle_group_lbl)
                muscleGroupRecycler.visibility = View.VISIBLE
                exercisesRecycler.visibility = View.GONE
                buttonsContainer.visibility = View.GONE
            }
            Mode.SELECT_EXERCISE -> {
                actionSpinner.visibility = View.GONE
                title.visibility = View.VISIBLE
                title.text = requireContext().getString(R.string.select_exercise_lbl)
                muscleGroupRecycler.visibility = View.GONE
                exercisesRecycler.visibility = View.VISIBLE
                buttonsContainer.visibility = View.VISIBLE
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
}
