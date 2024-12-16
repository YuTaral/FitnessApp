package com.example.fitnessapp.dialogs

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.R
import com.example.fitnessapp.utils.Utils
import java.util.Calendar
import java.util.Date

/** Custom date and time picker dialog */
class DateTimePickerDialog(ctx: Context): BaseDialog(ctx) {
    override var layoutId = R.layout.date_picker_dialog

    private lateinit var onSaveCallback: (Date) -> Unit
    private lateinit var datePicker: CalendarView
    private lateinit var timePicker: TimePicker
    private lateinit var datePickerContainer: ConstraintLayout
    private lateinit var timePickerContainer: ConstraintLayout
    private lateinit var previousBtn: Button
    private lateinit var nextBtn: Button
    private lateinit var saveBtn: Button

    override fun findViews() {
        datePickerContainer = dialogView.findViewById(R.id.date_picker_container)
        timePickerContainer = dialogView.findViewById(R.id.time_picker_container)
        datePicker = dialogView.findViewById(R.id.date_picker)
        timePicker = dialogView.findViewById(R.id.time_picker)
        previousBtn = dialogView.findViewById(R.id.previous_btn)
        nextBtn = dialogView.findViewById(R.id.next_btn)
        saveBtn = dialogView.findViewById(R.id.save_btn)
    }

    override fun populateDialog() {
        title.text = Utils.defaultFormatDateTime(Date())
        timePicker.setIs24HourView(true)
    }

    override fun addClickListeners() {
        datePicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Create calendar object, setting the year, month and day
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

            // Change the displayed as title date
            title.text = Utils.defaultFormatDateTime(calendar.time)

            // Set the selected date in the view itself
            datePicker.date = calendar.timeInMillis
        }

        timePicker.setOnTimeChangedListener { _, hours, minutes ->
            // Create calendar object, setting the hours and minutes
            val calendar = Calendar.getInstance()

            // Get the selected date from datePicker (CalendarView)
            val selectedDateInMillis = datePicker.date
            calendar.timeInMillis = selectedDateInMillis

            // Set the selected hours and minutes from the timePicker
            calendar.set(Calendar.HOUR_OF_DAY, hours)
            calendar.set(Calendar.MINUTE, minutes)

            // Change the displayed as title date
            title.text = Utils.defaultFormatDateTime(calendar.time)
        }

        nextBtn.setOnClickListener {
            datePickerContainer.visibility = View.GONE
            timePickerContainer.visibility = View.VISIBLE
        }

        previousBtn.setOnClickListener {
            timePickerContainer.visibility = View.GONE
            datePickerContainer.visibility = View.VISIBLE
        }

        saveBtn.setOnClickListener {
            var date = Utils.parseDateTime(title.text.toString())

            if (date == null) {
                // If parsing fails, use the current date time
                date = Date()
            }

            onSaveCallback(date)
        }
    }

    /** Setter for the on save callback */
    fun setOnSaveCallback(callback: (Date) -> Unit) {
        onSaveCallback = callback
    }
}