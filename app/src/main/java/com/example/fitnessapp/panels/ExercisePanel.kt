package com.example.fitnessapp.panels

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.MGExercisesRecyclerAdapter
import com.example.fitnessapp.adapters.MuscleGroupRecyclerAdapter
import com.example.fitnessapp.dialogs.AddExerciseDialog
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.network.repositories.MuscleGroupRepository

/** Class to hold the logic for Exercise Panel */
class ExercisePanel: PanelFragment()  {
    override var id: Long = 4
    override var layoutId: Int = R.layout.exercise_panel
    override var panelIndex: Int = 2
    override var titleId: Int = R.string.exercise_lbl

    private lateinit var muscleGroupRecycler: RecyclerView
    private lateinit var exercisesRecycler: RecyclerView
    private lateinit var title: TextView
    private lateinit var buttonsContainer: ConstraintLayout
    private lateinit var backBtn: Button
    private lateinit var addBtn: Button
    private lateinit var muscleGroups: MutableList<MuscleGroupModel>
    private var selectedMuscleGroupId: Long = 0

    override fun initializePanel() {
        muscleGroups = mutableListOf()

        // Find the views
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
            selectedMuscleGroupId = 0
            displayMuscleGroupOrExercises(true)
        }
        addBtn.setOnClickListener{ AddExerciseDialog(AddExerciseDialog.Mode.CREATE_NEW, null, selectedMuscleGroupId).showDialog() }
    }

    /** Populates the data in the panel */
    private fun populatePanel() {
        displayMuscleGroupOrExercises(true)

        if (muscleGroups.isEmpty()) {
            // Fetch the muscle groups if the list is empty
            MuscleGroupRepository().getMuscleGroups(onSuccess = { data ->
                muscleGroups = data

                muscleGroupRecycler.layoutManager = LinearLayoutManager(context)
                muscleGroupRecycler.adapter = MuscleGroupRecyclerAdapter(muscleGroups,
                    callback = { muscleGroupId ->
                        selectedMuscleGroupId = muscleGroupId

                        ExerciseRepository().getMuscleGroupExercises(muscleGroupId, onSuccess = { exercises ->
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
        displayMuscleGroupOrExercises(false)

        exercisesRecycler.layoutManager = LinearLayoutManager(context)

        if (exercises.isEmpty()) {
            // Show title that there is no exercises for this muscle group
            title.text = requireContext().getString(R.string.no_exercise_lbl)

            if (initializeAdapter) {
                exercisesRecycler.adapter = MGExercisesRecyclerAdapter(mutableListOf())
            } else {
                (exercisesRecycler.adapter as MGExercisesRecyclerAdapter).updateData(mutableListOf())
            }
        } else {
            // Display the exercises
            if (initializeAdapter) {
                exercisesRecycler.adapter = MGExercisesRecyclerAdapter(exercises)
            } else {
                (exercisesRecycler.adapter as MGExercisesRecyclerAdapter).updateData(exercises)
            }
        }
    }

    /** Changes the visibility the views in the panel to display the muscle groups or exercises
     * @param mg true if muscle groups recycler should be visible, false if
     * exercises recycler should be visible
     */
    private fun displayMuscleGroupOrExercises(mg: Boolean) {
        if (mg) {
            title.text = requireContext().getString(R.string.select_muscle_group_lbl)
            muscleGroupRecycler.visibility = View.VISIBLE
            exercisesRecycler.visibility = View.GONE
            buttonsContainer.visibility = View.GONE
        } else {
            title.text = requireContext().getString(R.string.select_exercise_lbl)
            muscleGroupRecycler.visibility = View.GONE
            exercisesRecycler.visibility = View.VISIBLE
            buttonsContainer.visibility = View.VISIBLE
        }
    }
}
