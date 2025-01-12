package com.example.fitnessapp.panels

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.WorkoutsRecAdapter
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.dialogs.DateTimePickerDialog
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils
import java.util.Calendar
import java.util.Date

/** Main Panel class to implement the logic in the main panel, where workouts are displayed */
class WorkoutsPanel: BasePanel() {
    override var id: Long = Constants.PanelUniqueId.MAIN.ordinal.toLong()
    override var layoutId: Int = R.layout.panel_main
    override var iconId = R.drawable.icon_tab_workouts
    override var panelIndex: Int = Constants.PanelIndices.MAIN.ordinal
    override var titleId: Int = R.string.workouts_panel_title

    private lateinit var fromDateContainer: ConstraintLayout
    private lateinit var fromDateLbl: TextView
    private lateinit var noWorkoutsLbl: TextView
    private lateinit var workoutsRecycler: RecyclerView
    private lateinit var newWorkoutBtn: Button

    private lateinit var startDate: Date

    /** Used to track when change in the selected workout occurred and
     *  latest workouts refresh is needed
     */
    var refreshWorkouts = false

    override fun onResume() {
        super.onResume()

        if (refreshWorkouts) {
            populatePanel()
        }
    }

    override fun findViews() {
        fromDateContainer = panel.findViewById(R.id.workouts_from_date_container)
        fromDateLbl = panel.findViewById(R.id.workout_filter_lbl)
        noWorkoutsLbl = panel.findViewById(R.id.no_workouts_lbl)
        workoutsRecycler = panel.findViewById(R.id.workouts_recycler)
        newWorkoutBtn = panel.findViewById(R.id.new_workout_btn)
    }

    override fun populatePanel() {
        // Default start date for workouts fetch to -1 month backwards
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        startDate = calendar.time

        fromDateLbl.text = String.format(Utils.getActivity().getString(R.string.workouts_filter_lbl), Utils.defaultFormatDate(startDate))

        populateWorkouts()
    }

    override fun addClickListeners() {
        fromDateContainer.setOnClickListener { selectDate() }
        newWorkoutBtn.setOnClickListener { AddEditWorkoutDialog(requireContext(), AddEditWorkoutDialog.Mode.ADD).show() }
    }

    /** Send request to fetch the workouts */
    private fun populateWorkouts() {
        WorkoutRepository().getWorkouts(startDate, onSuccess = { returnData ->
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

    /** Select the workouts start date */
    private fun selectDate() {
        val dialog = DateTimePickerDialog(requireContext())

        dialog.setOnSaveCallback { date ->
            dialog.dismiss()
            startDate = date
            fromDateLbl.text = String.format(Utils.getActivity().getString(R.string.workouts_filter_lbl), Utils.defaultFormatDate(startDate))

            populateWorkouts()
        }

        dialog.show()
    }
}