package com.example.fitnessapp.adapters

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.fitnessapp.interfaces.IExercisePanel
import com.example.fitnessapp.interfaces.IFragmentRefreshListener
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.panels.MainPanel
import com.example.fitnessapp.panels.ManageExercisesPanel
import com.example.fitnessapp.panels.PanelFragment
import com.example.fitnessapp.panels.SelectedWorkoutPanel
import com.example.fitnessapp.utils.StateEngine

/** FragmentStateAdapter used to manage the panels */
class PanelAdapter(pagerView: ViewPager2, fragmentActivity: FragmentActivity, count: Int) : FragmentStateAdapter(fragmentActivity) {
    /** The pager view */
    private var pager: ViewPager2 = pagerView

    /** The main panel instance */
    private lateinit var mainPanel: MainPanel

    /** The workout panel instance */
    private lateinit var workoutPanel: SelectedWorkoutPanel

    /** The temporary panel instance */
    private var temporaryPanel: PanelFragment? = null

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
            else -> { getTemporaryPanel() }
        }

        return fragment
    }

    override fun getItemId(position: Int): Long {
        // Override the method to send the unique id to containsItem fun
        return when (position) {
            0 -> { getMainPanel().getUniqueId() }
            1 -> { getWorkoutPanel().getUniqueId() }
            else -> { getTemporaryPanel().getUniqueId() }
        }
    }

    override fun containsItem(itemId: Long): Boolean {
        // When there is active temporary panel,
        // and we are adding a new one, check the unique id of the panel
        // otherwise the FragmentStateAdapter does not recognize the change,
        // because the panels count remains the same, and the new panel is
        // not being created
        if (temporaryPanel != null) {
            return getTemporaryPanel().getUniqueId() == itemId
        }

        return true
    }


    /** Initializes the main panel if it's not and returns the instance */
    private fun getMainPanel(): PanelFragment {
        if (!::mainPanel.isInitialized) {
            mainPanel = MainPanel()
        }

        return mainPanel
    }

    /** Initializes the workout panel if it's not and returns the instance */
    private fun getWorkoutPanel(): PanelFragment {
        if (!::workoutPanel.isInitialized) {
            workoutPanel = SelectedWorkoutPanel()
        }

        return workoutPanel
    }

    /** Returns the temporary panel instance */
    private fun getTemporaryPanel(): PanelFragment {
        return temporaryPanel as PanelFragment
    }

    /** Returns the temporary panel instance as IExercisePanel */
    fun getIExercisePanel(): IExercisePanel? {
        if (temporaryPanel is IExercisePanel) {
            return temporaryPanel as IExercisePanel
        }
        return null
    }

    /** Returns the temporary panel instance as Manage Exercise Panel */
    fun getManageExercisesPanel(): ManageExercisesPanel? {
        if (temporaryPanel == null || temporaryPanel !is ManageExercisesPanel) {
            return null
        }

        return temporaryPanel as ManageExercisesPanel
    }

    /** Removes the currently created temporary panel, when navigating away from it
     * or a new temporary panel must be created
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun removeTemporaryPanel() {
        if (temporaryPanel != null) {
            fragmentCount --
            notifyDataSetChanged()
            temporaryPanel = null
        }
    }

    /** Displays Workout panel  */
    fun displayWorkoutPanel() {
        pager.setCurrentItem(getWorkoutPanel().getIndex(), true)
    }

    /** Displays Workout panel
     * @param workout the workout model, updates the StateEngine variable
     * @param refreshWorkouts true if StateEngine variable to refresh workouts should be set to true,
     * false otherwise. If null provided, the variable is not being changed
     */
    fun displayWorkoutPanel(workout: WorkoutModel, refreshWorkouts: Boolean?) {
        val index = getWorkoutPanel().getIndex()

        StateEngine.workout = workout

        if (refreshWorkouts != null) {
            StateEngine.refreshWorkouts = refreshWorkouts
        }

        if (index != pager.currentItem) {
            // If the index of the viewPager changes, this will trigger onResume(), which will re-populate
            // the panel
            pager.setCurrentItem(index, true)
        } else {
            // If the index of the viewPager does not change, this will not trigger onResume(), notify the listener
            // to re-populate the panel
            (getWorkoutPanel() as IFragmentRefreshListener).onRefreshListener()
        }
    }

    /** Displays Main panel
     *  @param refreshWorkouts true if StateEngine variable to refresh workouts should be set to true,
     * false otherwise
     */
    fun displayMainPanel(refreshWorkouts: Boolean) {
        StateEngine.refreshWorkouts = refreshWorkouts
        pager.setCurrentItem(getMainPanel().getIndex(), true)
    }

    /** Displays temporary panel
     * @param panel - the temporary panel panel
     */
    @SuppressLint("NotifyDataSetChanged")
    fun displayTemporaryPanel(panel: PanelFragment) {
        // Remove the previous temporary panel
        removeTemporaryPanel()

        // Set the temporary panel
        temporaryPanel = panel

        // Increase the fragments counter and notify the adapter that change occurred
        fragmentCount ++
        notifyDataSetChanged()

        pager.setCurrentItem(temporaryPanel!!.getIndex(), true)
    }

    /** Callback to be executed when Panel selection changes
     * @param position the panel position
     */
     fun onPanelSelectionChange(position: Int) {
         if (fragmentCount > initialFragmentCount && (position == getMainPanel().getIndex() || position == getWorkoutPanel().getIndex())) {
             removeTemporaryPanel()
         }
     }

    /** Returns the panel title to be displayed in the tab layout
     * @param position the panel position
     */
    fun getPanelTitle(position: Int): String {
        return when (position) {
            0 -> { getMainPanel().getTitle() }
            1 -> { getWorkoutPanel().getTitle() }
            else -> { getTemporaryPanel().getTitle() }
        }
    }
}