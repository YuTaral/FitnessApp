package com.example.fitnessapp.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.example.fitnessapp.R
import com.example.fitnessapp.utils.Utils

/** Base Alert dialog class to implement the common logic for all alert dialogs */
abstract class BaseAlertDialog(ctx: Context): AlertDialog(ctx, R.style.Theme_FitnessApp_AlertDialog) {
    /** The dialog layout */
    protected open var layoutId = 0

    /** The dialog view */
    protected lateinit var dialogView: View

    /** The close icon displayed at the top right */
    protected lateinit var closeIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the dialog layout
        dialogView = LayoutInflater.from(Utils.getContext()).inflate(layoutId, null)

        // Execute the logic to find views, populate the data and add click listeners
        findCommonAndSpecificViews()
        populateDialog()
        addCommonAndSpecificClickListeners()

        // Set the content
        setContentView(dialogView)
    }

    /** Find the common views and call the method to find the dialog specific views */
    private fun findCommonAndSpecificViews() {
        closeIcon = dialogView.findViewById(R.id.dialog_close)

        findViews()
    }

    /** Add click listeners to the common views and call the method to add click listeners for
     * the dialog specific views */
    private fun addCommonAndSpecificClickListeners() {
        closeIcon.setOnClickListener { dismiss() }

        addClickListeners()
    }

    /** Find the views in the dialog*/
    abstract fun findViews()

    /** Populate the data in the dialog */
    abstract fun populateDialog()

    /** Add click listeners to the views */
    abstract fun addClickListeners()
}