package com.example.fitnessapp.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.utils.Utils

/** Base dialog class to implement the common logic for all dialogs */
abstract class BaseDialog(ctx: Context): Dialog(ctx, R.style.Theme_FitnessApp_Dialog) {
    /** The dialog layout */
    protected open var layoutId = 0

    /** The dialog title id */
    protected open var dialogTitleId: Int = 0

    /** The dialog view */
    protected lateinit var dialogView: View

    /** The dialog title */
    protected lateinit var title: TextView

    /** The close icon displayed at the top right */
    protected lateinit var closeIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the dialog layout
        dialogView = LayoutInflater.from(Utils.getContext()).inflate(layoutId, null)

        // Find the views
        findViews()

        // Set the title
        if (dialogTitleId > 0) {
            title.text = Utils.getContext().getString(dialogTitleId)
        }

        // Populate the dialog
        populateDialog()

        // Add click listeners
        addClickListeners()

        // Set the content
        setContentView(dialogView)

        // Set cancelable to false, we have close icon on each dialog
        setCancelable(false)

        Utils.getMainActivity().activeDialogs.add(this)
    }

    override fun dismiss() {
        super.dismiss()
        Utils.getMainActivity().activeDialogs.remove(this)
    }

    /** Find the views in the dialog*/
    open fun findViews() {
        title = dialogView.findViewById(R.id.dialog_title)
        closeIcon = dialogView.findViewById(R.id.dialog_close)
    }

    /** Populate the data in the dialog */
    abstract fun populateDialog()

    /** Add click listeners to the views */
    open fun addClickListeners() {
        closeIcon.setOnClickListener { dismiss() }
    }
}