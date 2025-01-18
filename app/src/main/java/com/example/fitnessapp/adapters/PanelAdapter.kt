package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.interfaces.ITemporaryPanel
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.panels.BaseExercisePanel
import com.example.fitnessapp.panels.BasePanel
import com.example.fitnessapp.panels.BaseTeamPanel
import com.example.fitnessapp.panels.ManageExercisesPanel
import com.example.fitnessapp.panels.ManageTeamsPanel
import com.example.fitnessapp.panels.ManageTemplatesPanel
import com.example.fitnessapp.panels.NotificationsPanel
import com.example.fitnessapp.panels.SelectedWorkoutPanel
import com.example.fitnessapp.panels.WorkoutsPanel
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** FragmentStateAdapter used to manage the panels */
class PanelAdapter(pagerView: ViewPager2, fragmentActivity: FragmentActivity, count: Int) : FragmentStateAdapter(fragmentActivity) {
    /** The pager view */
    private var pager: ViewPager2 = pagerView

    /** The workouts panel instance */
    private lateinit var workoutsPanel: WorkoutsPanel

    /** The workout panel instance */
    private lateinit var workoutPanel: SelectedWorkoutPanel

    private var temporaryPanels: MutableList<BasePanel> = mutableListOf()

    /** The number of initial fragments count*/
    private var initialFragmentCount: Int = count

    /** The number of currently created fragments */
    private var fragmentCount: Int = count

    override fun getItemCount(): Int {
        return fragmentCount
    }

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = when (position) {
            0 -> { getWorkoutsPanel() }
            1 -> { getWorkoutPanel() }
            else -> { getTemporaryPanel(position) }
        }

        return fragment
    }

    override fun getItemId(position: Int): Long {
        // Override the method to send the unique id to containsItem fun
        return when (position) {
            0 -> { getWorkoutsPanel().getUniqueId() }
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

    /** Initialize the main panel if it's not and return the instance */
    private fun getWorkoutsPanel(): WorkoutsPanel {
        if (!::workoutsPanel.isInitialized) {
            workoutsPanel = WorkoutsPanel()
        }

        return workoutsPanel
    }

    /** Initialize the workout panel if it's not and return the instance */
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

    /** Retrieve a temporary panel at a specified position and casts it to the specified type.
     * This method is a generic helper that allows fetching a temporary panel by its position
     * and ensures the panel is of the desired type. If the type does not match or no panel exists
     * at the specified position, it returns null
     * @param T The desired type of the panel (must inherit from BasePanel)
     * @param position The position of the temporary panel in the adapter
     */
    private inline fun <reified T : BasePanel> getTemporaryPanelAs(position: Int): T? {
        if (temporaryPanels.isNotEmpty()) {
            val panel = getTemporaryPanel(position)
            if (panel is T) return panel
        }
        return null
    }

    /** Return the temporary panel instance as ManageTeamsPanel */
    private fun getTeamsPanel(): ManageTeamsPanel? = getTemporaryPanelAs(Constants.PanelIndices.FIRST_TEMPORARY.ordinal)

    /** Return the temporary panel instance as BaseExercisePanel */
    fun getBaseExercisePanel(): BaseExercisePanel? = getTemporaryPanelAs(Constants.PanelIndices.FIRST_TEMPORARY.ordinal)

    /** Return the temporary panel instance as AddEditTeamPanel */
    fun getTeamPanel(): BaseTeamPanel? = getTemporaryPanelAs(Constants.PanelIndices.SECOND_TEMPORARY.ordinal)

    /** Return the temporary panel instance as ManageTeamsPanel */
    fun getManageTeamsPanel(): ManageTeamsPanel? = getTemporaryPanelAs(Constants.PanelIndices.FIRST_TEMPORARY.ordinal)

    /** Return the temporary panel instance as ManageTemplatesPanel */
    fun getManageTemplatesPanel(): ManageTemplatesPanel? = getTemporaryPanelAs(Constants.PanelIndices.FIRST_TEMPORARY.ordinal)

    /** Return the temporary panel instance as NotificationsPanel */
    fun getNotificationsPanel(): NotificationsPanel? = getTemporaryPanelAs(Constants.PanelIndices.FIRST_TEMPORARY.ordinal)

    /** Return true if the current active panel is Manage Exercise, false otherwise */
    fun isManageExerciseActive(): Boolean {
        val panel: ManageExercisesPanel? = getTemporaryPanelAs(Constants.PanelIndices.FIRST_TEMPORARY.ordinal)

        return panel != null
    }

    /** Remove the created temporary panel, when navigating away from it
     * or a new temporary panel must be created
     * @param position the position of the temporary panel to remove, -1 if all must be removed
     */
    @SuppressLint("NotifyDataSetChanged")
    fun removeTemporaryPanels(position: Int) {
        if (temporaryPanels.isEmpty()) {
            return
        }

        if (getNotificationsPanel() != null) {
            // Set notification button at the top header as inactive if it is being removed
            (Utils.getActivity() as MainActivity).setNotificationButtonInactive()
        }

        if (position == -1) {
            fragmentCount -= temporaryPanels.size
            temporaryPanels.clear()
        } else {
            fragmentCount --
            temporaryPanels.removeAt(getTempPanelPosition(position))
        }

        notifyDataSetChanged()
    }

    /** Return the temporary panel position in the temporaryPanels list from it's position in the
     * fragment state adapter
     * @param position the panel position in the fragment state adapter
     */
    private fun getTempPanelPosition(position: Int): Int {
        return position - initialFragmentCount
    }

    /** Refresh Workout panel
     * @param refreshWorkouts true if StateEngine variable to refresh workouts should be set to true,
     * false otherwise. If null provided, the variable is not being changed
     */
    fun refreshWorkoutPanel(refreshWorkouts: Boolean?) {
        refreshWorkoutPanel(AppStateManager.workout, refreshWorkouts)
    }

    /** Refresh Workout panel
     * @param workout the workout model, updates the StateEngine variable
     * @param refreshWorkouts true if StateEngine variable to refresh workouts should be set to true,
     * false otherwise. If null provided, the variable is not being changed
     */
    fun refreshWorkoutPanel(workout: WorkoutModel?, refreshWorkouts: Boolean?) {
        val index = getWorkoutPanel().getIndex()

        AppStateManager.workout = workout

        if (refreshWorkouts != null) {
            getWorkoutsPanel().refreshWorkouts = refreshWorkouts
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

    /** Refresh the Workouts panel */
    fun refreshWorkoutsPanel() {
        getWorkoutsPanel().refreshWorkouts = true

        val index = getWorkoutsPanel().getIndex()

        if (index != pager.currentItem) {
            // If the index of the viewPager changes, this will trigger onResume(), which will re-populate
            // the panel
            pager.setCurrentItem(index, true)
        } else {
            // If the index of the viewPager does not change, this will not trigger onResume(), notify the listener
            // to re-populate the panel
            getWorkoutsPanel().populatePanel()
        }
    }

    /** Display temporary panel
     * @param panel - the temporary panel panel
     */
    @SuppressLint("NotifyDataSetChanged")
    fun displayTemporaryPanel(panel: BasePanel) {
        // Remove the previous temporary panel(s)
        if ((panel as ITemporaryPanel).removePreviousTemporary) {
            removeTemporaryPanels(-1)

        } else if (temporaryPanels.size == 2 && panel is BaseTeamPanel) {
            // The second temporary panel must be removed in case we are adding Add or Edit Team panel
            // and there is already active second temporary panel
            removeTemporaryPanels(Constants.PanelIndices.SECOND_TEMPORARY.ordinal)
        }

        // Set the temporary panel
        temporaryPanels.add(panel)

        // Increase the fragments counter and notify the adapter that change occurred
        fragmentCount ++
        notifyDataSetChanged()

        pager.setCurrentItem(getTemporaryPanel(panel.getIndex()).getIndex(), true)
    }

    /** Refresh the teams panel after add / update / delete team */
    fun refreshTeamsPanel() {
        // Set the refresh variable
        getTeamsPanel()!!.setRefreshTeams(true)

        // Remove the 2nd temporary panel if there is
        removeTemporaryPanels(Constants.PanelIndices.SECOND_TEMPORARY.ordinal)

        // Set the current item
        pager.currentItem = temporaryPanels[getTempPanelPosition(Constants.PanelIndices.FIRST_TEMPORARY.ordinal)].getIndex()
    }

    /** Callback to be executed when Panel selection changes
     * @param position the panel position
     */
     fun onPanelSelectionChange(position: Int) {
         if (fragmentCount > initialFragmentCount &&
             (position == getWorkoutsPanel().getIndex() || position == getWorkoutPanel().getIndex())) {

             removeTemporaryPanels( -1)
         }
     }

    /** Return the panel title to be displayed in the tab layout
     * @param position the panel position
     */
    fun getPanelTitle(position: Int): String {
        return when (position) {
            0 -> { getWorkoutsPanel().getTitle() }
            1 -> { getWorkoutPanel().getTitle() }
            else -> { getTemporaryPanel(position).getTitle() }
        }
    }

    /** Return the panel icon to be displayed in the tab layout
     * @param position the panel position
     */
    fun getPanelIcon(position: Int): Int {
        return when (position) {
            0 -> { getWorkoutsPanel().getIcon() }
            1 -> { getWorkoutPanel().getIcon() }
            else -> { getTemporaryPanel(position).getIcon() }
        }
    }

    /** Refresh the panel when the current weight unit has been changed */
    fun refreshIfUnitChanged() {
        when (pager.currentItem) {
            getWorkoutsPanel().getIndex() -> {
                // Refresh the main panel to update the weight unit displayed in the workout summary
                refreshWorkoutsPanel()
            }
            getWorkoutPanel().getIndex() -> {
                // Refresh the workout panel now and workouts panel later
                refreshWorkoutPanel(true)
            }
            else -> {
                // If temporary panel is active, refresh only the workouts, the panel is updated only
                // when refreshWorkouts is set to true. Workout panel is updated each time it is selected
                // and the units are auto updated
                getWorkoutsPanel().refreshWorkouts = true
            }
        }
    }
}