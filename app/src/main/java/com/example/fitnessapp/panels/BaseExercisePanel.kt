package com.example.fitnessapp.panels

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.MGExercisesRecyclerAdapter
import com.example.fitnessapp.adapters.MuscleGroupRecyclerAdapter
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.MuscleGroupModel
import com.example.fitnessapp.network.repositories.ExerciseRepository
import com.example.fitnessapp.network.repositories.MuscleGroupRepository
import com.example.fitnessapp.utils.Utils

/** Base ExercisePanel Class to implement the common logic for exercise panels:
 * - ExercisePanel where exercise can be selected and added to the current workout
 * - ManageExercisesPanel, where exercises can be edited
 */
abstract class BaseExercisePanel(mode: Mode): PanelFragment() {
    /** Parameter value which is send when fetching the exercises for muscle group */
    protected open lateinit var onlyForUser: String

    /** The string id to show when no exercises for the selected muscle group are found */
    protected open var noExercisesStringId: Int = 0

    /** Enum to hold the different states of the panel */
    enum class Mode {
        SELECT_MUSCLE_GROUP,
        SELECT_EXERCISE,
    }

    private lateinit var selectedMuscleGroupLbl: TextView
    private lateinit var search: EditText
    private lateinit var buttonsContainer: ConstraintLayout
    private lateinit var backBtn: Button
    private lateinit var muscleGroups: MutableList<MuscleGroupModel>
    protected lateinit var title: TextView
    protected lateinit var actionSpinner: Spinner
    protected lateinit var addBtn: Button
    protected lateinit var muscleGroupRecycler: RecyclerView
    protected lateinit var exercisesRecycler: RecyclerView
    protected var selectedMuscleGroup: MuscleGroupModel? = null
    protected var panelMode: Mode = mode

    override fun initializePanel() {
        // Initialize the common views between all exercise panels
        muscleGroups = mutableListOf()

        // Find the views
        title = panel.findViewById(R.id.exercise_panel_title)
        actionSpinner = panel.findViewById(R.id.exercise_action_spinner)
        search = panel.findViewById(R.id.search)
        selectedMuscleGroupLbl = panel.findViewById(R.id.selected_muscle_group_lbl)
        muscleGroupRecycler = panel.findViewById(R.id.muscle_groups_recycler)
        exercisesRecycler = panel.findViewById(R.id.exercises_recycler)
        buttonsContainer = panel.findViewById(R.id.buttons_container)
        backBtn = panel.findViewById(R.id.back_btn)
        addBtn = panel.findViewById(R.id.add_btn)

        // Disable the state restoration in case the exercise panel is recreated, we need
        // the search to be empty on each recreation
        search.isSaveEnabled = false

        // Perform the additional initialization
        additionalPanelInitialization()

        // Populate the panel
        populatePanel()

        // Add search functionality
        addSearch()

        // Click listeners
        backBtn.setOnClickListener {
            // Clear the search if it's not empty to reset the data in the currently active adapter
            clearSearch()

            // Update the mode, reset the selectedMuscleGroupId and update the views
            panelMode = Mode.SELECT_MUSCLE_GROUP
            selectedMuscleGroup = null
            updateViews()
        }
    }

    /** Populate the data in the panel */
    private fun populatePanel() {
        updateViews()

        if (muscleGroups.isEmpty()) {
            // Fetch the muscle groups if the list is empty
            MuscleGroupRepository().getMuscleGroups(onSuccess = { data ->
                muscleGroups = data

                muscleGroupRecycler.layoutManager = LinearLayoutManager(context)
                muscleGroupRecycler.adapter = MuscleGroupRecyclerAdapter(muscleGroups,
                    callback = { muscleGroup ->
                        selectedMuscleGroup = muscleGroup

                        ExerciseRepository().getMuscleGroupExercises(selectedMuscleGroup!!.id, onlyForUser, onSuccess = { exercises ->
                            populateExercises(exercises, true)
                        })
                    })
            })
        }
    }

    /** Populate the exercises of the given muscle group
     * @param exercises the exercises to display
     * @param initializeAdapter true if the adapter should be initialized, false if we just want
     * to update the data
     */
    fun populateExercises(exercises: List<MGExerciseModel>, initializeAdapter: Boolean) {
        // Clear the search if it's not empty to reset the data in the currently active adapter
        clearSearch()

        // Set the mode
        panelMode = Mode.SELECT_EXERCISE

        // Update the views after the mode has been set
        updateViews()

        if (exercises.isEmpty()) {
            // Show no exercises
            showViewsWhenNoExercises(initializeAdapter)
        } else {
            selectedMuscleGroupLbl.text = String.format(Utils.getContext()
                .getString(R.string.exercises_for_mg_lbl), selectedMuscleGroup!!.name)

            // Display the exercises
            displayExercises(exercises, initializeAdapter)
        }
    }

    /** Execute the logic to update views there are exercises for the selected muscle group
     * @param exercises the exercises to display
     * @param initializeAdapter true if the recycler adapter needs initialization, false otherwise
     */
    private fun displayExercises(exercises: List<MGExerciseModel>, initializeAdapter: Boolean) {
        if (initializeAdapter) {
            exercisesRecycler.layoutManager = LinearLayoutManager(context)

            exercisesRecycler.adapter = MGExercisesRecyclerAdapter(exercises, callback = { model ->
                onExerciseSelectCallback(model)
            })

        } else {
            (exercisesRecycler.adapter as MGExercisesRecyclerAdapter).updateData(exercises)
        }
    }

    /** Add search functionality to search for Muscle Groups / Exercises */
    private fun addSearch() {
        search.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                when (panelMode) {
                    Mode.SELECT_MUSCLE_GROUP -> {
                        if (muscleGroupRecycler.adapter != null) {
                            // Filter the muscle groups by the text
                            (muscleGroupRecycler.adapter as MuscleGroupRecyclerAdapter).filter(s.toString().lowercase())
                        }
                    }
                    Mode.SELECT_EXERCISE -> {
                        if (exercisesRecycler.adapter != null) {
                            // Filter the exercises by the provided text
                            (exercisesRecycler.adapter as MGExercisesRecyclerAdapter).filter(s.toString().lowercase())
                        }
                    }
                }
            }
        })
    }

    /** Execute the logic to update views when no exercises has been found for the selected muscle group
     * @param initializeAdapter true if the recycler adapter needs initialization, false otherwise
     */
    private fun showViewsWhenNoExercises(initializeAdapter: Boolean) {
        // Show title that there is no exercises for this muscle group
        selectedMuscleGroupLbl.text = String.format(Utils.getContext()
                .getString(noExercisesStringId), selectedMuscleGroup!!.name)

        if (initializeAdapter) {
            exercisesRecycler.layoutManager = LinearLayoutManager(context)
            exercisesRecycler.adapter = MGExercisesRecyclerAdapter(mutableListOf(), callback = {})
        } else {
            (exercisesRecycler.adapter as MGExercisesRecyclerAdapter).updateData(mutableListOf())
        }
    }

    /** Change the visibility the views in the panel to display the muscle groups or exercises */
    private fun updateViews() {
        when (panelMode) {
            Mode.SELECT_MUSCLE_GROUP -> {
                selectedMuscleGroupLbl.visibility = View.GONE
                muscleGroupRecycler.visibility = View.VISIBLE
                exercisesRecycler.visibility = View.GONE
                buttonsContainer.visibility = View.GONE
            }
            Mode.SELECT_EXERCISE -> {
                selectedMuscleGroupLbl.visibility = View.VISIBLE
                muscleGroupRecycler.visibility = View.GONE
                exercisesRecycler.visibility = View.VISIBLE
                buttonsContainer.visibility = View.VISIBLE
            }
        }

        // Update the specific views
        updateAdditionalViews()
    }

    /** Clear the search field if not empty */
    private fun clearSearch() {
        if (search.text.isNotEmpty()) {
            search.setText("")
        }
    }

    /** Perform the additional initialization specific for the exercise panel */
    abstract fun additionalPanelInitialization()

    /** Perform update on the panel exercise panel specific views */
    abstract fun updateAdditionalViews()

    /** Callback to execute when user selects exercise
     * @param model the selected exercise
     */
    abstract fun onExerciseSelectCallback(model: MGExerciseModel)
}