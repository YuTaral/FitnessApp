package com.example.fitnessapp.dialogs

import android.content.Context
import android.os.Build
import android.widget.Button
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Utils
import java.time.Duration
import java.util.Date

/** Dialog to select date and time when the workout is finished */
class FinishWorkoutDialog(ctx: Context): BaseDialog(ctx) {
    override var layoutId = R.layout.finish_workout_dialog
    override var dialogTitleId = R.string.finish_workout_lbl

    private lateinit var dateTime: TextView
    private lateinit var saveBtn: Button

    override fun findViews() {
        dateTime = dialogView.findViewById(R.id.workout_finish_date_time_txt)
        saveBtn = dialogView.findViewById(R.id.save_btn)
    }

    override fun populateDialog() {
        dateTime.text = Utils.defaultFormatDateTime(Date())

    }

    override fun addClickListeners() {
        dateTime.setOnClickListener {
            // Open the date picker dialog
            val dialog = DateTimePickerDialog(Utils.getContext())

            dialog.setOnSaveCallback{ date ->
                // Change the value on save
                dateTime.text = Utils.defaultFormatDateTime(date)
                dialog.dismiss()
            }

            dialog.show()
        }

        saveBtn.setOnClickListener {
            // Create new workout object, changing the finish date time
            val workout = AppStateManager.workout!!
            val finishedDateTime =  Utils.parseDateTime(dateTime.text.toString())

            // Make sure the finished time is not earlier than the started time
            if (AppStateManager.workout!!.startDateTime!! > finishedDateTime) {
                Utils.showToast(R.string.error_msg_finish_time)
                return@setOnClickListener
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Duration.between is supported after Android Oreo
                workout.durationSeconds = Duration.between(
                                AppStateManager.workout!!.startDateTime!!.toInstant(), finishedDateTime!!.toInstant())
                                .seconds.toInt()
            } else {
                workout.durationSeconds = 0
            }

            // Set the finish dateTime
            workout.finishDateTime = finishedDateTime

            // Edit the workout to mark it as finished
            WorkoutRepository().editWorkout(workout, onSuccess = { updatedWorkout ->
                dismiss()
                AppStateManager.panelAdapter.displayWorkoutPanel(updatedWorkout, true)
            })
        }
    }
}