package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.models.BaseModel
import com.example.fitnessapp.models.ExerciseModel
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Utils

/** Dialog used to ask a question and execute a callback on confirm */
@SuppressLint("InflateParams")
class AskQuestionDialog(ctx: Context, q: Question, d: BaseModel? = null): BaseDialog(ctx) {
    /** Enum to hold all questions */
    enum class Question(private val titleId: Int, private val questionId: Int,
                        private val yesBtnTextId: Int, private val noBtnTextId: Int) {

        DELETE_TEMPLATE(R.string.question_delete_template_title, R.string.question_delete_template_text, R.string.yes_btn, R.string.no_btn),
        DELETE_MG_EXERCISE(R.string.question_delete_exercise_title, R.string.question_delete_exercise_text, R.string.yes_btn, R.string.no_btn),
        OVERRIDE_EXISTING_EXERCISE(R.string.question_override_exercise_title, R.string.question_override_exercise_text, R.string.override_btn, R.string.create_new_btn),
        LOG_OUT(R.string.q_log_out_title, R.string.q_log_out_text, R.string.yes_btn, R.string.no_btn),
        DELETE_WORKOUT(R.string.question_delete_workout_title, R.string.question_delete_workout_text, R.string.yes_btn, R.string.no_btn),
        DELETE_EXERCISE_FROM_WORKOUT(R.string.question_delete_exercise_from_workout_title, R.string.question_delete_exercise_from_workout_text, R.string.yes_btn, R.string.no_btn),
        FINISH_WORKOUT(R.string.question_finish_workout_title, R.string.question_finish_workout_text, R.string.yes_btn, R.string.no_btn),
        WORKOUT_ALREADY_FINISHED_WHEN_ADD_EXERCISE(R.string.question_workout_already_finished_title, R.string.question_workout_already_finished_text, R.string.yes_btn, R.string.no_btn),
        WORKOUT_ALREADY_FINISHED_WHEN_EDIT_EXERCISE(R.string.question_workout_already_finished_title, R.string.question_workout_already_finished_when_edit_text, R.string.yes_btn, R.string.no_btn),
        PROFILE_IMAGE_SELECTION(R.string.question_choose_profile_image_title, R.string.question_choose_profile_image_text, R.string.camera_btn, R.string.gallery_btn),
        ALLOW_CAMERA_PERMISSION(R.string.question_go_to_settings_title, R.string.question_go_to_settings_text, R.string.go_to_settings_btn, R.string.no_btn);

        /** Returns the question title */
        fun getTitle(): String {
            return Utils.getMainActivity().getString(titleId)
        }

        /** Returns the question text */
        fun getQuestionText(): String {
            return Utils.getMainActivity().getString(questionId)
        }

        /** Returns the Yes button text */
        fun getYesText(): String {
            return Utils.getMainActivity().getString(yesBtnTextId)
        }

        /** Returns the No button text */
        fun getNoText(): String {
            return Utils.getMainActivity().getString(noBtnTextId)
        }
    }

    override var layoutId = R.layout.ask_question_dialog

    private var question = q
    private var data = d

    private lateinit var onYesCallback: () -> Unit
    private lateinit var onNoCallback: () -> Unit
    private lateinit var questionText: TextView
    private lateinit var questionAdditionalInfo: TextView
    private lateinit var yesBtn: Button
    private lateinit var noBtn: Button

    /** Setter for the callback which will be executed on confirm button click */
    fun setYesCallback(callback: () -> Unit) {
        onYesCallback = callback
    }

    /** Setter for the callback which will be executed on cancel button click */
    fun setNoCallback(callback: () -> Unit) {
        onNoCallback = callback
    }

    override fun findViews() {
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        questionText = dialogView.findViewById(R.id.question_lbl)
        questionAdditionalInfo = dialogView.findViewById(R.id.question_additional_info)
        yesBtn = dialogView.findViewById(R.id.yes_btn)
        noBtn = dialogView.findViewById(R.id.no_btn)
    }

    override fun populateDialog() {
        var formatName = ""

        title.text = question.getTitle()

        // Populate the data based on the question
        when (question) {
            Question.DELETE_TEMPLATE -> {
                formatName = (data as WorkoutModel).name
            }

            Question.DELETE_MG_EXERCISE -> {
                formatName = (data as MGExerciseModel).name
            }

            Question.OVERRIDE_EXISTING_EXERCISE -> {
                val model = data as MGExerciseModel

                formatName = model.name
                questionAdditionalInfo.visibility = View.VISIBLE
                questionAdditionalInfo.text = String.format(Utils.getContext().getString(R.string.mg_exercise_description), model.description)
            }

            Question.DELETE_WORKOUT -> {
                formatName = (data as WorkoutModel).name
            }

            Question.DELETE_EXERCISE_FROM_WORKOUT -> {
                formatName = (data as ExerciseModel).name
            }

            Question.FINISH_WORKOUT -> {
                formatName = if (workoutAllSetsCompleted()) {
                    ""
                } else {
                    Utils.getContext().getString(R.string.exercises_not_finished_lbl)
                }
            }

            else -> {
                // Nothing to do
            }
        }

        questionText.text = String.format(question.getQuestionText(), formatName)

        // Set button text
        yesBtn.text = question.getYesText()
        noBtn.text = question.getNoText()
    }

    override fun addClickListeners() {
        yesBtn.setOnClickListener { onYesCallback() }

        if (::onNoCallback.isInitialized) {
            noBtn.setOnClickListener { onNoCallback() }
        } else {
            noBtn.setOnClickListener { dismiss() }
        }
    }

    /** Perform a check whether all exercises sets in the current workout are completed and
     * return true or false
     */
    private fun workoutAllSetsCompleted(): Boolean {

        for (e: ExerciseModel in AppStateManager.workout!!.exercises) {
            if (e.sets.any { !it.completed }) {
                return false
            }
        }

        return true
    }
}