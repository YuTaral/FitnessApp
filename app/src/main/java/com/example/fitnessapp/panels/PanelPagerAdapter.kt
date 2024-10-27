package com.example.fitnessapp.panels

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fitnessapp.R
import com.example.fitnessapp.utils.Utils

class PanelPagerAdapter(fragmentActivity: FragmentActivity, private val itemCount: Int) : FragmentStateAdapter(fragmentActivity) {
    /** Enum to hold all panel types */
    enum class Panel(private val index: Int, private val titleId: Int) {
        MAIN(0, R.string.main_panel_title),
        WORKOUT(1, R.string.workout_panel_title);

        /** Returns the panel index */
        fun getIndex(): Int {
            return index
        }

        /** Returns the panel title */
        fun getTitle(): String {
            return Utils.getActivity().getString(titleId)
        }
    }

    override fun getItemCount(): Int = itemCount

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MainPanel()
            else -> SelectedWorkoutPanel()
        }
    }
}