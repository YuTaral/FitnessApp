package com.example.fitnessapp.panels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.fitnessapp.R
import com.example.fitnessapp.utils.Utils

/** Abstract BasePanel class to define the common logic for the panels displayed in the top
 * tab layout. Each panel in the tab layout should inherit this class
 */
abstract class BasePanel: Fragment() {
    /** The panel root view */
    protected lateinit var panel: View

    /** The panel id */
    protected abstract var id: Long

    /** The index in the tab layout */
    protected abstract var panelIndex: Int

    /** The panel title id */
    protected abstract var titleId: Int

    /** The panel icon id */
    protected abstract var iconId: Int

    /** The panel layout id */
    protected abstract var layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        panel = inflater.inflate(R.layout.panel_base, container, false)

        // Inflate the child layout (the one specific to the panel)
        setMainContent()

        // Execute the logic to find views, populate the data and add click listeners
        findViews()
        populatePanel()
        addClickListeners()

        return panel
    }

    /** Set the main content of the dialog */
    private fun setMainContent() {
        // Find the main content container
        val dialogMainContent = panel.findViewById<ConstraintLayout>(R.id.panel_main_content)

        // Inflate the child layout (the one specific to the panel)
        val contentView = LayoutInflater.from(Utils.getActivity()).inflate(layoutId, null)

        // Set layout params for content view to match parent width and height
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT
        )

        // Set the main content
        dialogMainContent.addView(contentView, layoutParams)
    }

    /** Find the views in the panel. No need to call the method when the panel is being created,
     * it is called in the base class
     */
    abstract fun findViews()

    /** Populates the data in the panel. No need to call the method when the panel is being created,
     * it is called in the base class
     */
    abstract fun populatePanel()

    /** Add click listeners to the views. No need to call the method when the panel is being created,
     * it is called in the base class
     */
    abstract fun addClickListeners()

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

    /** Returns the panel icon to show in the tab layout */
    fun getIcon(): Int {
        return iconId
    }
}