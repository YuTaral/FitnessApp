package com.example.fitnessapp.panels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fitnessapp.utils.Utils

/** Abstract PanelFragment class. Each panel in the tab layout should inherit this class */
abstract class PanelFragment: Fragment() {
    /** The panel root view */
    protected lateinit var panel: View

    /** The panel id */
    protected abstract var id: Long

    /** The index in the tab layout */
    protected abstract var panelIndex: Int

    /** The panel title id */
    protected abstract var titleId: Int

    /** The panel layout id */
    protected abstract var layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        panel = inflater.inflate(layoutId, container, false)

        initializePanel()

        return panel
    }

    /** Find the views in the panel, add click listeners and populate the data */
    abstract fun initializePanel()

    /** Returns the panel unique id */
    fun getUniqueId(): Long {
        return id
    }

    /** Returns the panel index */
    fun getIndex(): Int {
        return panelIndex
    }

    /** Returns the panel title to show in the tab layout */
    fun getTitle(): String {
        return Utils.getActivity().getString(titleId)
    }
}