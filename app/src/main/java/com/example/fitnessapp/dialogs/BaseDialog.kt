package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.R
import com.example.fitnessapp.utils.Utils

/** Base dialog class to implement the common logic for all dialogs */
abstract class BaseDialog(ctx: Context): Dialog(ctx, R.style.Theme_FitnessApp_Dialog) {
    /** The dialog layout */
    protected open var layoutId = 0

    /** The dialog title id */
    protected open var dialogTitleId: Int = 0

    /** The dialog root view */
    protected lateinit var dialog: ConstraintLayout

    /** The dialog title */
    protected lateinit var title: TextView

    /** The close icon displayed at the top right */
    protected lateinit var closeIcon: ImageView

    /** The loading spinner */
    private lateinit var loadingSpinner: FrameLayout

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the base dialog layout
        dialog = LayoutInflater.from(Utils.getActivity()).inflate(R.layout.dialog_base, null) as ConstraintLayout

        // Set the dialog main content layout
        setMainContent()

        // Display loading spinner until th dialog is being initialized
        handleLoadingSpinner(true)

        // Find the views
        findViews()

        // Set the title
        if (dialogTitleId > 0) {
            title.text = Utils.getActivity().getString(dialogTitleId)
        }

        // Populate the dialog
        populateDialog()

        // Add click listeners
        addClickListeners()

        // Set the content
        setContentView(dialog)

        // Set cancelable to false, we have close icon on each dialog
        setCancelable(false)

        Utils.getActivity().activeDialogs.add(this)

        // Remove the loading spinner after the dialog is initialized
        handleLoadingSpinner(false)
    }

    override fun dismiss() {
        super.dismiss()
        Utils.getActivity().activeDialogs.remove(this)
    }

    /** Set the main content of the dialog */
    private fun setMainContent() {
        // Find the main content container
        val dialogMainContent = dialog.findViewById<ConstraintLayout>(R.id.dialog_main_content)

        // Inflate the child layout (the one specific to the dialog)
        val contentView = LayoutInflater.from(Utils.getActivity()).inflate(layoutId, null)

        // Set layout params for content view to match parent width
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )

        // Set the main content
        dialogMainContent.addView(contentView, layoutParams)
    }

    /** Display the loading spinner until the dialog is populated and event listeners are added
     * @param display true to display it, false to hide it
     */
    private fun handleLoadingSpinner(display: Boolean) {
        if (display)  {
            loadingSpinner = dialog.findViewById(R.id.loading_container)
            loadingSpinner.visibility = View.VISIBLE
        } else {
            loadingSpinner.visibility = View.GONE
        }
    }

    /** Find the views in the dialog */
    open fun findViews() {
        title = dialog.findViewById(R.id.dialog_title)
        closeIcon = dialog.findViewById(R.id.dialog_close)
    }

    /** Populate the data in the dialog */
    abstract fun populateDialog()

    /** Add click listeners to the views */
    open fun addClickListeners() {
        closeIcon.setOnClickListener { dismiss() }
    }
}