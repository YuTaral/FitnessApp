package com.example.fitnessapp.panels

import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.CustomSpinnerAdapter
import com.example.fitnessapp.adapters.WorkoutsRecAdapter
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Main Panel class to implement the logic in the main panel, where workouts are displayed */
class WorkoutsPanel: BasePanel() {
    override var id: Long = Constants.PanelUniqueId.MAIN.ordinal.toLong()
    override var layoutId: Int = R.layout.panel_main
    override var iconId = R.drawable.icon_tab_workouts
    override var panelIndex: Int = Constants.PanelIndices.MAIN.ordinal
    override var titleId: Int = R.string.workouts_panel_title

    private var selectedFilter = Filters.ALL
    private var isSpinnerInitialized = false

    private lateinit var noWorkoutsLbl: TextView
    private lateinit var workoutsRecycler: RecyclerView
    private lateinit var newWorkoutBtn: Button
    private lateinit var filterSpinner: Spinner

    /** Used to track when change in the selected workout occurred and
     *  latest workouts refresh is needed
     */
    var refreshWorkouts = false

    /** Workout filter values */
    enum class Filters(private val stringId: Int) {
        ALL(R.string.workout_filter_all),
        IN_PROGRESS(R.string.workout_filter_in_progress),
        COMPLETED(R.string.workout_filter_completed);

        override fun toString(): String {
            return Utils.getActivity().getString(stringId)
        }
    }

    override fun onResume() {
        super.onResume()

        if (refreshWorkouts) {
            populatePanel()
        }
    }

    override fun findViews() {
        filterSpinner = panel.findViewById(R.id.workout_filter_spinner)
        noWorkoutsLbl = panel.findViewById(R.id.no_workouts_lbl)
        workoutsRecycler = panel.findViewById(R.id.workouts_recycler)
        newWorkoutBtn = panel.findViewById(R.id.new_workout_btn)
    }

    override fun populatePanel() {
        if (filterSpinner.adapter == null) {
            filterSpinner.adapter = CustomSpinnerAdapter(requireContext(), false, listOf(
                Filters.ALL.toString(),
                Filters.IN_PROGRESS.toString(),
                Filters.COMPLETED.toString(),
            ))
        }

        if (filterSpinner.adapter != null && filterSpinner.selectedItemPosition != Filters.ALL.ordinal) {
            // Change the selected filter value if it's not All
            selectedFilter = Filters.valueOf(filterSpinner.selectedItem.toString().uppercase().replace(" ", "_"))
        }

        populateWorkouts()
    }

    override fun addClickListeners() {
        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!isSpinnerInitialized) {
                    // Ignore the initial call which is triggered when the spinner is initialized
                    // with the adapter
                    isSpinnerInitialized = true
                    return
                }

                // Change the selected filter value and fetch the workouts
                selectedFilter = Filters.entries[position]
                populateWorkouts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        newWorkoutBtn.setOnClickListener { AddEditWorkoutDialog(requireContext(), AddEditWorkoutDialog.Mode.ADD).show() }
    }

    /** Send request to fetch the workouts */
    private fun populateWorkouts() {
        WorkoutRepository().getWorkouts(selectedFilter.name, onSuccess = { returnData ->
            val workouts = returnData.map { WorkoutModel(it) }.toMutableList()

            if (workouts.isEmpty()) {
                workoutsRecycler.visibility = View.GONE
                noWorkoutsLbl.visibility = View.VISIBLE

            } else {
                noWorkoutsLbl.visibility = View.GONE
                workoutsRecycler.visibility = View.VISIBLE

                workoutsRecycler.adapter = WorkoutsRecAdapter(workouts) { workout ->
                    Utils.getPanelAdapter().refreshWorkoutPanel(workout, null)
                }
            }

            // The most recent data with workouts is now displayed
            refreshWorkouts = false
        })
    }
}