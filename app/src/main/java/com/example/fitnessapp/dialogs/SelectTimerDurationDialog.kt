package com.example.fitnessapp.dialogs

import android.content.Context
import android.widget.Button
import android.widget.NumberPicker
import com.example.fitnessapp.R
import com.example.fitnessapp.utils.Utils

/** Dialog containing hours, minutes and seconds to allow the user to select duration and start timer */
class SelectTimerDurationDialog(ctx: Context): BaseDialog(ctx) {
    override var layoutId = R.layout.dialog_select_timer_duration
    override var dialogTitleId = R.string.select_duration_lbl

    private lateinit var hours: NumberPicker
    private lateinit var minutes: NumberPicker
    private lateinit var seconds: NumberPicker
    private lateinit var startBtn: Button

    override fun findViews() {
        super.findViews()

        hours = dialog.findViewById(R.id.hour_picker)
        minutes = dialog.findViewById(R.id.minute_picker)
        seconds = dialog.findViewById(R.id.second_picker)
        startBtn = dialog.findViewById(R.id.start_timer_btn)
    }

    override fun populateDialog() {
        hours.minValue = 0
        hours.maxValue = 23

        minutes.minValue = 0
        minutes.maxValue = 60

        seconds.minValue = 0
        seconds.maxValue = 60
    }

    override fun addClickListeners() {
        super.addClickListeners()

        startBtn.setOnClickListener {
            startTimer()
        }
    }

    /** Start the timer on button click */
    private fun startTimer() {
        // Calculate the seconds and start the timer
        val seconds = (hours.value * 3600) + (minutes.value * 60) + seconds.value + 1

        if (seconds == 0) {
            Utils.showMessageWithVibration(R.string.error_select_duration)
            return
        }

        dismiss()

        TimerDialog(Utils.getActivity(), R.string.timer_title_lbl, seconds, true).show()
    }
}