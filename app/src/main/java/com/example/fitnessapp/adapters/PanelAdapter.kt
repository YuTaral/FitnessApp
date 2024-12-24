package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.panels.AddEditTeamPanel
import com.example.fitnessapp.panels.BaseExercisePanel
import com.example.fitnessapp.panels.BasePanel
import com.example.fitnessapp.panels.MainPanel
import com.example.fitnessapp.panels.ManageExercisesPanel
import com.example.fitnessapp.panels.ManageTeamsPanel
import com.example.fitnessapp.panels.SelectedWorkoutPanel
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** FragmentStateAdapter used to manage the panels */
class PanelAdapter(pagerView: ViewPager2, fragmentActivity: FragmentActivity, count: Int) : FragmentStateAdapter(fragmentActivity) {
    /** The pager view */
    private var pager: ViewPager2 = pagerView

    /** The main panel instance */
    private lateinit var mainPanel: MainPanel

    /** The workout panel instance */
    private lateinit var workoutPanel: SelectedWorkoutPanel

    private var temporaryPanels: MutableList<BasePanel> = mutableListOf()

    /** Holds the number of initial fragments count*/
    private var initialFragmentCount: Int = count

    /** Holds the number of currently created fragments */
    private var fragmentCount: Int = count

    override fun getItemCount(): Int {
        return fragmentCount
    }

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = when (position) {
            0 -> { getMainPanel() }
            1 -> { getWorkoutPanel() }
            else -> { getTemporaryPanel(position) }
        }

        return fragment
    }

    override fun getItemId(position: Int): Long {
        // Override the method to send the unique id to containsItem fun
        return when (position) {
            0 -> { getMainPanel().getUniqueId() }
            1 -> { getWorkoutPanel().getUniqueId() }
            else -> { getTemporaryPanel(position).getUniqueId() }
        }
    }

    override fun containsItem(itemId: Long): Boolean {
        // When there is active temporary panel,
        // and we are adding a new one, check the unique id of the panel
        // otherwise the FragmentStateAdapter does not recognize the change,
        // because the panels count remains the same, and the new panel is
        // not being created
        if (temporaryPanels.isNotEmpty()) {
            return temporaryPanels[temporaryPanels.size - 1].getUniqueId() == itemId
        }

        return true
    }

    /** Initialize the main panel if it's not and returns the instance */
    fun getMainPanel(): MainPanel {
        if (!::mainPanel.isInitialized) {
            mainPanel = MainPanel()
        }

        return mainPanel
    }

    /** Initialize the workout panel if it's not and returns the instance */
    private fun getWorkoutPanel(): BasePanel {
        if (!::workoutPanel.isInitialized) {
            workoutPanel = SelectedWorkoutPanel()
        }

        return workoutPanel
    }

    /** Return the temporary panel instance at the specified position
     * @param position the position of the panel in the adapter
     */
    private fun getTemporaryPanel(position: Int): BasePanel {
        return temporaryPanels[getTempPanelPosition(position)]
    }

    /** Return the temporary panel instance as BaseExercisePanel */
    fun getBaseExercisePanel(): BaseExercisePanel? {
        if (temporaryPanels.isNotEmpty()) {
            val exercisePanel = temporaryPanels[getTempPanelPosition(Constants.PanelIndices.TEMPORARY.ordinal)]

            if (exercisePanel is BaseExercisePanel) {
                return exercisePanel
            }
        }

        return null
    }

    /** Return the temporary panel instance as ManageTeamsPanel */
    fun getTeamsPanel(): ManageTeamsPanel? {
        if (temporaryPanels.isNotEmpty()) {
            val exercisePanel = temporaryPanels[getTempPanelPosition(Constants.PanelIndices.TEMPORARY.ordinal)]

            if (exercisePanel is ManageTeamsPanel) {
                return exercisePanel
            }
        }

        return null
    }

    /** Return the temporary panel instance as AddEditTeamPanel */
    fun getTeamPanel(): AddEditTeamPanel? {
        if (temporaryPanels.isNotEmpty()) {
            val exercisePanel = temporaryPanels[getTempPanelPosition(Constants.PanelIndices.ANOTHER_TEMPORARY.ordinal)]

            if (exercisePanel is AddEditTeamPanel) {
                return exercisePanel
            }
        }

        return null
    }

    /** Return true if the current active panel is Manage Exercise, false otherwise */
    fun isManageExerciseActive(): Boolean {
        if (temporaryPanels.isNotEmpty()) {
            val exercisePanel = temporaryPanels[getTempPanelPosition(Constants.PanelIndices.TEMPORARY.ordinal)]

            return exercisePanel is ManageExercisesPanel
        }

        return false
    }

    /** Remove the created temporary panel, when navigating away from it
     * or a new temporary panel must be created
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun removeTemporaryPanels() {
        if (temporaryPanels.isEmpty()) {
            return
        }

        fragmentCount -= temporaryPanels.size
        temporaryPanels.clear()
        notifyDataSetChanged()
    }

    /** Return the temporary panel position in the temporaryPanels list from it's position in the
     * fragment state adapter
     * @param position the panel position in the fragment state adapter
     */
    private fun getTempPanelPosition(position: Int): Int {
        return position - initialFragmentCount
    }

    /** Display Workout panel
     * @param refreshWorkouts true if StateEngine variable to refresh workouts should be set to true,
     * false otherwise. If null provided, the variable is not being changed
     */
    fun displayWorkoutPanel(refreshWorkouts: Boolean?) {
        displayWorkoutPanel(AppStateManager.workout, refreshWorkouts)
    }

    /** Display Workout panel
     * @param workout the workout model, updates the StateEngine variable
     * @param refreshWorkouts true if StateEngine variable to refresh workouts should be set to true,
     * false otherwise. If null provided, the variable is not being changed
     */
    fun displayWorkoutPanel(workout: WorkoutModel?, refreshWorkouts: Boolean?) {
        val index = getWorkoutPanel().getIndex()

        AppStateManager.workout = workout

        if (refreshWorkouts != null) {
            Utils.setRefreshWorkouts(refreshWorkouts)
        }

        if (index != pager.currentItem) {
            // If the index of the viewPager changes, this will trigger onResume(), which will re-populate
            // the panel
            pager.setCurrentItem(index, true)
        } else {
            // If the index of the viewPager does not change, this will not trigger onResume(), notify the listener
            // to re-populate the panel
            getWorkoutPanel().populatePanel()
        }
    }

    /** Display Main panel
     *  @param refreshWorkouts true if StateEngine variable to refresh workouts should be set to true,
     * false otherwise
     */
    fun displayMainPanel(refreshWorkouts: Boolean) {
        Utils.setRefreshWorkouts(refreshWorkouts)

        val index = getMainPanel().getIndex()

        if (index != pager.currentItem) {
            // If the index of the viewPager changes, this will trigger onResume(), which will re-populate
            // the panel
            pager.setCurrentItem(index, true)
        } else {
            // If the index of the viewPager does not change, this will not trigger onResume(), notify the listener
            // to re-populate the panel
            getMainPanel().populatePanel()
        }
    }

    /** Display temporary panel
     * @param panel - the temporary panel panel
     */
    @SuppressLint("NotifyDataSetChanged")
    fun displayTemporaryPanel(panel: BasePanel) {
        // Remove the previous temporary panel(s)
        if ((panel as ITemporaryPanel).removePreviousTemporary) {
            removeTemporaryPanels()
        }

        // Set the temporary panel
        temporaryPanels.add(panel)

        // Increase the fragments counter and notify the adapter that change occurred
        fragmentCount ++
        notifyDataSetChanged()

        pager.setCurrentItem(getTemporaryPanel(panel.getIndex()).getIndex(), true)
    }

    /** Callback to be executed when Panel selection changes
     * @param position the panel position
     */
     fun onPanelSelectionChange(position: Int) {
         if (fragmentCount > initialFragmentCount &&
             (position == getMainPanel().getIndex() || position == getWorkoutPanel().getIndex())) {

             removeTemporaryPanels()
         }
     }

    /** Return the panel title to be displayed in the tab layout
     * @param position the panel position
     */
    fun getPanelTitle(position: Int): String {
        return when (position) {
            0 -> { getMainPanel().getTitle() }
            1 -> { getWorkoutPanel().getTitle() }
            else -> { getTemporaryPanel(position).getTitle() }
        }
    }

    /** Refresh the panel when the current weight unit has been changed */
    fun refreshIfUnitChanged() {
        when (pager.currentItem) {
            getMainPanel().getIndex() -> {
                // Refresh the main panel to update the weight unit displayed in the workout summary
                displayMainPanel(true)
            }
            getWorkoutPanel().getIndex() -> {
                // Refresh the workout panel now and workouts panel later
                displayWorkoutPanel(true)
            }
            else -> {
                // If temporary panel is active, refresh only the workouts, the panel is updated only
                // when refreshWorkouts is set to true. Workout panel is updated each time it is selected
                // and the units are auto updated
                Utils.setRefreshWorkouts(true)
            }
        }
    }
}