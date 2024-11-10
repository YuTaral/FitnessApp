package com.example.fitnessapp.panels

import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.MuscleGroupRecyclerAdapter
import com.example.fitnessapp.network.repositories.MuscleGroupRepository
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for Exercise Panel */
class ExercisePanel(m: PanelMode): PanelFragment()  {
    override var id: Long = 4
    override var layoutId: Int = R.layout.exercise_panel
    override var panelIndex: Int = 2
    override var titleId: Int = R.string.exercise_lbl

    /** Enum to hold different modes for the panel */
    enum class PanelMode {
        ADD,
        EDIT
    }

    private lateinit var muscleGroupRecycler: RecyclerView
    private lateinit var nextBtn: Button
    private var panelMode = m

    override fun initializePanel() {
        // Find the views
        muscleGroupRecycler = panel.findViewById(R.id.muscle_groups_recycler)
        nextBtn = panel.findViewById(R.id.next_btn)

        // Populate the panel
        populatePanel(panelMode)

        // Click listeners
        nextBtn.setOnClickListener { }
    }

    /** Populates the data in the panel
     * @param mode the panel mode
     * @param overrideMode whether panelMode property should be overridden, false by default
     * */
    private fun populatePanel(mode: PanelMode, overrideMode: Boolean = false) {
        if (overrideMode) {
            panelMode = mode
        }

        MuscleGroupRepository().getMuscleGroups(onSuccess = { muscleGroups ->
            muscleGroupRecycler.layoutManager = LinearLayoutManager(Utils.getContext())
            muscleGroupRecycler.adapter = MuscleGroupRecyclerAdapter(muscleGroups, true)
        })
    }
}