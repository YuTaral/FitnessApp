package com.example.fitnessapp.panels

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.fitnessapp.R
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** FragmentStateAdapter used to manage the panels */
class PanelAdapter(pagerView: ViewPager2, fragmentActivity: FragmentActivity, initialFragmentsCount: Int) : FragmentStateAdapter(fragmentActivity) {
    /** Enum to hold all panel types */
    enum class Panel(private val index: Int, private val titleId: Int) {
        MAIN(0, R.string.main_panel_title),
        WORKOUT(1, R.string.workout_panel_title),
        TEMPLATES(2, R.string.templates_lbl);

        /** Returns the panel index */
        fun getIndex(): Int {
            return index
        }

        /** Returns the panel title */
        fun getTitle(): String {
            return Utils.getActivity().getString(titleId)
        }
    }

    /** The pager view */
    private var pager: ViewPager2 = pagerView

    /** Holds the number of currently created fragments */
    private var fragmentCount: Int = initialFragmentsCount

    /** Holds the TemplatesPanel mode */
    private lateinit var templatesPanelMode: TemplatesPanel.Mode

    override fun getItemCount(): Int {
        return fragmentCount
    }

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment = when (position) {
            0 -> { MainPanel() }
            1 -> { SelectedWorkoutPanel() }
            else -> {
                TemplatesPanel(templatesPanelMode)
            }
        }

        return fragment
    }

    /** Displays Workout panel  */
    fun displayWorkoutPanel() {
        pager.setCurrentItem(Panel.WORKOUT.getIndex(), true)
    }

    /** Displays Workout panel
     * @param workout the workout model, updates the StateEngine variable
     * @param refreshWorkouts true if StateEngine variable to refresh workouts should be set to true,
     * false otherwise. If null provided, the variable is not being changed
     */
    fun displayWorkoutPanel(workout: WorkoutModel, refreshWorkouts: Boolean?) {
        val index = Panel.WORKOUT.getIndex()

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
            val fragment = Utils.getActivity().supportFragmentManager.findFragmentByTag("f$index") as FragmentRefreshListener
            fragment.onRefreshListener()
        }
    }

    /** Displays Main panel
     *  @param refreshWorkouts true if StateEngine variable to refresh workouts should be set to true,
     * false otherwise
     */
    fun displayMainPanel(refreshWorkouts: Boolean) {
        StateEngine.refreshWorkouts = refreshWorkouts
        pager.setCurrentItem(Panel.MAIN.getIndex(), true)
    }

    /** Displays Templates panel
     * @param mode - the templates panel mode
     * */
    fun displayTemplatesPanel(mode: TemplatesPanel.Mode) {
        templatesPanelMode = mode
        val index = Panel.TEMPLATES.getIndex()

        // Increase the fragments counter
        if (fragmentCount < Panel.entries.size) {
            fragmentCount ++
        }

        // Create the fragment
        createFragment(index)
        notifyItemChanged(index)

        if (index != pager.currentItem) {
            // If the index of the viewPager changes, this will trigger the onCreateView, which will populate the panel
            pager.setCurrentItem(index, true)
        } else {
            // There is a possible scenario when Templates Panel is the current active fragment,
            // and it is being reopened (e.g opened by clicking Templates in the Main Panel and then re-opened
            // from the right drawer). In this case the index of the viewPager does not change,
            // which will not trigger the onCreateView() with the correct callback for template click
            // Find the fragment and populate it, overriding the OnClick method
            val fragment = Utils.getActivity().supportFragmentManager.findFragmentByTag("f$index") as TemplatesPanel
            fragment.populatePanel(templatesPanelMode, true)
        }
    }

    /** Callback to be executed when Panel selection changes
     * @param position the current position
     * */
     fun onPanelSelectionChange(position: Int) {
        if (position != Panel.TEMPLATES.getIndex() && fragmentCount == Panel.entries.size) {
            fragmentCount --
            notifyItemRemoved(Panel.TEMPLATES.getIndex())
        }
     }
}