package com.example.fitnessapp.panels

import android.animation.LayoutTransition
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.ExercisesRecAdapter
import com.example.fitnessapp.dialogs.AddEditWorkoutDialog
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.Date


/** New Workout Panel class to display and manage the selected workout */
class SelectedWorkoutPanel : BasePanel() {
    override var id: Long = Constants.PanelUniqueId.WORKOUT.ordinal.toLong()
    override var layoutId: Int = R.layout.panel_selected_workout
    override var panelIndex: Int = Constants.PanelIndices.WORKOUT.ordinal
    override var titleId: Int = R.string.workout_panel_title
    override var iconId = R.drawable.icon_tab_selected_workout

    private lateinit var mainContent: ConstraintLayout
    private lateinit var noWorkoutContent: ConstraintLayout
    private lateinit var workoutDuration: TextView
    private lateinit var workoutName: TextView
    private lateinit var workoutDate: TextView
    private lateinit var workoutStatus: TextView
    private lateinit var showMoreOrLessNotes: TextView
    private lateinit var notes: TextView
    private lateinit var exerciseRecycler: RecyclerView
    private lateinit var newExerciseBtn: Button
    private lateinit var editBtn: Button

    private var timerJob: Job? = null
    private var secondsElapsed: Int = 0

    override fun onResume() {
        super.onResume()
        populatePanel()
    }

    override fun findViews() {
        mainContent = panel.findViewById(R.id.main_content)
        noWorkoutContent = panel.findViewById(R.id.no_workout_content)
        workoutDuration = panel.findViewById(R.id.duration)
        workoutName = panel.findViewById(R.id.current_workout_label)
        workoutStatus = panel.findViewById(R.id.current_workout_status_value_lbl)
        workoutDate = panel.findViewById(R.id.current_workout_date_label)
        showMoreOrLessNotes = panel.findViewById(R.id.show_more_or_less_notes)
        notes = panel.findViewById(R.id.notes)
        exerciseRecycler = panel.findViewById(R.id.exercises_recycler)
        newExerciseBtn = panel.findViewById(R.id.add_exercise_btn)
        editBtn = panel.findViewById(R.id.edit_btn)
    }

    override fun populatePanel() {
        val statusStringId: Int
        val statusColorId: Int

        if (AppStateManager.workout != null) {
            mainContent.visibility = View.VISIBLE
            noWorkoutContent.visibility = View.GONE

            workoutName.text = AppStateManager.workout!!.name
            workoutDate.text = Utils.defaultFormatDate(AppStateManager.workout!!.startDateTime!!)

             if (AppStateManager.workout!!.finishDateTime == null) {
                 statusStringId = R.string.in_progress_lbl
                 statusColorId = R.color.orange
            } else {
                 statusStringId = R.string.finished_lbl
                 statusColorId = R.color.green
            }

            workoutStatus.text = Utils.getActivity().getString(statusStringId)
            workoutStatus.setTextColor(requireContext().getColor(statusColorId))

            if (AppStateManager.workout!!.notes.isNotEmpty()) {
                notes.visibility = View.VISIBLE
                notes.maxLines = 3
                notes.text = AppStateManager.workout!!.notes

                if (notes.text.lines().size > 2) {
                    showMoreOrLessNotes.visibility = View.VISIBLE
                    showMoreOrLessNotes.text = Utils.getActivity().getText(R.string.show_more_lbl)
                } else {
                    showMoreOrLessNotes.visibility = View.GONE
                }
            } else {
                notes.visibility = View.GONE
                showMoreOrLessNotes.visibility = View.GONE
            }

        } else {
            mainContent.visibility = View.GONE
            noWorkoutContent.visibility = View.VISIBLE

            if (timerJob != null) {
                timerJob!!.cancel()
            }
        }

        if (exerciseRecycler.adapter == null) {
            exerciseRecycler.adapter = ExercisesRecAdapter(listOf())
        }

        if (AppStateManager.workout != null) {
            getExercisesRecyclerAdapter().updateData(AppStateManager.workout!!.exercises)
        }

        // Set buttons visibility
        setButtonsVisibility()

        // Set the duration timer
        if (AppStateManager.workout != null) {
           initializeDurationTimer()
        }
    }

    override fun addClickListeners() {
        newExerciseBtn.setOnClickListener {
            Utils.addEditExerciseClick(AskQuestionDialog.Question.WORKOUT_ALREADY_FINISHED_WHEN_ADD_EXERCISE, callback = {
                Utils.getPanelAdapter()
                    .displayTemporaryPanel(ExercisePanel(BaseExercisePanel.Mode.SELECT_MUSCLE_GROUP))
            })
        }

        editBtn.setOnClickListener {
            AddEditWorkoutDialog(requireContext(), AddEditWorkoutDialog.Mode.EDIT).show()
        }

        noWorkoutContent.setOnClickListener {
            AddEditWorkoutDialog(requireContext(), AddEditWorkoutDialog.Mode.ADD).show()
        }
    }

    override fun onPause() {
        super.onPause()

        if (timerJob != null) {
            timerJob!!.cancel()
        }
    }

    /** Return the exercises recycler adapter */
    private fun getExercisesRecyclerAdapter(): ExercisesRecAdapter {
        return exerciseRecycler.adapter as ExercisesRecAdapter
    }

    /** Sets buttons visibility based on whether workout is selected */
    private fun setButtonsVisibility() {
        if (AppStateManager.workout == null) {
            newExerciseBtn.visibility = View.GONE
            editBtn.visibility = View.GONE
        } else {
            newExerciseBtn.visibility = View.VISIBLE
            editBtn.visibility = View.VISIBLE

            if (showMoreOrLessNotes.visibility == View.VISIBLE) {
                // Add animation and click listener
                val transition = LayoutTransition().apply {
                    setDuration(500)
                    enableTransitionType(LayoutTransition.CHANGING)
                }
                (notes.parent as ConstraintLayout).layoutTransition = transition

                showMoreOrLessNotes.setOnClickListener {
                    showNotes()
                }
            }
        }
    }

    /** Click listener for Show more / Show less notes label */
    private fun showNotes() {
        if (showMoreOrLessNotes.text == Utils.getActivity().getText(R.string.show_more_lbl)) {
            showMoreOrLessNotes.text = Utils.getActivity().getText(R.string.show_less_lbl)
            notes.maxLines = Integer.MAX_VALUE
        } else {
            showMoreOrLessNotes.text = Utils.getActivity().getText(R.string.show_more_lbl)
            notes.maxLines = 3
        }
    }

    /** Initialize the workout duration timer */
    private fun initializeDurationTimer() {
        var hours: Int
        var minutes: Int
        var seconds: Int
        secondsElapsed = 0

        if (timerJob != null) {
            // Cancel the timer if it was initialized for previously selected workout
            timerJob!!.cancel()
        }

        if (AppStateManager.workout!!.finishDateTime == null) {
            // Workout not finished, add timer
            if (AppStateManager.workout!!.durationSeconds == 0) {

                // The workout has never been marked as finished, the duration is the time between workout start and now
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Duration.between is supported after Android Oreo
                    secondsElapsed = Duration.between(
                                        AppStateManager.workout!!.startDateTime!!.toInstant(), Date().toInstant())
                                        .seconds.toInt()

                    // If for any reason this returns negative number, set it to zero
                    // (could happen if AppStateManager.workout!!.startDateTime is in different timezone
                    // from Date()
                    if (secondsElapsed < 0) {
                        secondsElapsed = 0
                    }
                } else {
                    // Do not show the timer on older Android versions
                    workoutDuration.visibility = View.GONE
                    return
                }

            } else {
                // The workout was marked as finished, then marked as unfinished (in progress), the duration
                // to start the timer from is the previous duration
                secondsElapsed = AppStateManager.workout!!.durationSeconds!!
            }

            // Create the timer, refreshing it each second
            timerJob = CoroutineScope(Dispatchers.Main).launch {
                while (isActive) {
                    // Calculate the values and set the text
                    hours = secondsElapsed / 3600
                    minutes = (secondsElapsed % 3600) / 60
                    seconds = secondsElapsed % 60

                    workoutDuration.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                    // Increment the counter
                    secondsElapsed++

                    delay(1000) // Wait for 1 second before update
                }
            }
        } else {
            // Workout finished, just set the duration
            secondsElapsed = AppStateManager.workout!!.durationSeconds!!
            hours = secondsElapsed / 3600
            minutes = (secondsElapsed % 3600) / 60
            seconds = secondsElapsed % 60
            workoutDuration.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }
    }
}