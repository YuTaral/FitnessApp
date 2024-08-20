package com.example.fitnessapp.panels

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.utils.Utils


/** Class to hold the logic for the Main Panel */
class MainPanel(root: ViewGroup) {
    private val panel: View
    private val dateValue: TextView
    private val previousBtn: Button
    private val nextBtn: Button

    init {
        panel = LayoutInflater.from(Utils.getActivity()).inflate(R.layout.main_panel, root, false)

        // Find the views in the panel
        previousBtn = panel.findViewById(R.id.prev_btn)
        nextBtn = panel.findViewById(R.id.next_btn)
        dateValue = panel.findViewById(R.id.date_lbl)

        // Set default value to Today
        dateValue.text = Utils.getActivity().getText(R.string.today_lbl)

        // Add click listeners
        previousBtn.setOnClickListener { onPreviousClicked() }
        nextBtn.setOnClickListener { onNextClicked() }

        // Add the panel to the root view
        root.addView(panel)
    }

    /** Handles Previous button clicked */
    private fun onPreviousClicked() {
        dateValue.text = Utils.getActivity().getText(R.string.yesterday_lbl)
    }

    /** Handles Next button clicked */
    private fun onNextClicked() {
        dateValue.text = Utils.getActivity().getText(R.string.tomorrow_lbl)
    }
}