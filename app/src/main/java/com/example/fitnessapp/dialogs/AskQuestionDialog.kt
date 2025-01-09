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
import com.example.fitnessapp.models.TeamModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.utils.Utils

/** Dialog used to ask a question and execute a callback on confirm */
@SuppressLint("InflateParams")
class AskQuestionDialog(ctx: Context, q: Question, d: BaseModel? = null): BaseDialog(ctx) {
    /** Enum with all questions */
    enum class Question(private val titleId: Int, private val questionId: Int,
                        private val confirmButtonTextId: Int, private val cancelBtnTextId: Int) {

        DELETE_TEMPLATE(R.string.question_delete_template_title, R.string.question_delete_template_text, R.string.yes_btn, R.string.no_btn),
        DELETE_MG_EXERCISE(R.string.question_delete_exercise_title, R.string.question_delete_exercise_text, R.string.yes_btn, R.string.no_btn),
        OVERRIDE_EXISTING_EXERCISE(R.string.question_override_exercise_title, R.string.question_override_exercise_text, R.string.override_btn, R.string.create_new_btn),
        LOG_OUT(R.string.q_log_out_title, R.string.q_log_out_text, R.string.yes_btn, R.string.no_btn),
        DELETE_WORKOUT(R.string.question_delete_workout_title, R.string.question_delete_workout_text, R.string.yes_btn, R.string.no_btn),
        DELETE_EXERCISE_FROM_WORKOUT(R.string.question_delete_exercise_from_workout_title, R.string.question_delete_exercise_from_workout_text, R.string.yes_btn, R.string.no_btn),
        WORKOUT_ALREADY_FINISHED_WHEN_ADD_EXERCISE(R.string.question_workout_already_finished_title, R.string.question_workout_already_finished_text, R.string.yes_btn, R.string.no_btn),
        WORKOUT_ALREADY_FINISHED_WHEN_EDIT_EXERCISE(R.string.question_workout_already_finished_title, R.string.question_workout_already_finished_when_edit_text, R.string.yes_btn, R.string.no_btn),
        IMAGE_SELECTION_OPTIONS(R.string.question_choose_image_title, R.string.question_choose_image_text, R.string.camera_btn, R.string.gallery_btn),
        ALLOW_CAMERA_PERMISSION(R.string.question_go_to_settings_title, R.string.question_go_to_settings_text, R.string.go_to_settings_btn, R.string.no_btn),
        DELETE_TEAM(R.string.question_delete_team_title, R.string.question_delete_team_text, R.string.yes_btn, R.string.no_btn),
        GRANT_PERMISSIONS(R.string.question_grant_permissions, R.string.question_grant_permissions_text, R.string.view_permissions_btn, R.string.maybe_later_btn);

        /** Returns the question title */
        fun getTitle(): String {
            return Utils.getActivity().getString(titleId)
        }

        /** Returns the question text */
        fun getQuestionText(): String {
            return Utils.getActivity().getString(questionId)
        }

        /** Returns the confirm button text */
        fun getConfirmButtonText(): String {
            return Utils.getActivity().getString(confirmButtonTextId)
        }

        /** Returns the cancel button text */
        fun getCancelButtonText(): String {
            return Utils.getActivity().getString(cancelBtnTextId)
        }
    }

    override var layoutId = R.layout.dialog_ask_question

    private var question = q
    private var data = d

    private lateinit var onConfirmButtonClickCallback: () -> Unit
    private lateinit var onCancelButtonClickCallback: () -> Unit
    private lateinit var questionText: TextView
    private lateinit var questionAdditionalInfo: TextView
    private lateinit var confirmBtn: Button
    private lateinit var cancelBtn: Button

    /** Setter for the callback which will be executed on left button click */
    fun setConfirmButtonCallback(callback: () -> Unit) {
        onConfirmButtonClickCallback = callback
    }

    /** Setter for the callback which will be executed on right button click */
    fun setCancelButtonCallback(callback: () -> Unit) {
        onCancelButtonClickCallback = callback
    }

    override fun findViews() {
        super.findViews()

        questionText = dialog.findViewById(R.id.question_lbl)
        questionAdditionalInfo = dialog.findViewById(R.id.question_additional_info)
        confirmBtn = dialog.findViewById(R.id.yes_btn)
        cancelBtn = dialog.findViewById(R.id.no_btn)
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
                questionAdditionalInfo.text = String.format(Utils.getActivity().getString(R.string.mg_exercise_description), model.description)
            }

            Question.DELETE_WORKOUT -> {
                formatName = (data as WorkoutModel).name
            }

            Question.DELETE_EXERCISE_FROM_WORKOUT -> {
                formatName = (data as ExerciseModel).name
            }

            Question.DELETE_TEAM -> {
                formatName = (data as TeamModel).name
            }

            else -> {
                // Nothing to do
            }
        }

        questionText.text = String.format(question.getQuestionText(), formatName)

        // Set button text
        confirmBtn.text = question.getConfirmButtonText()
        cancelBtn.text = question.getCancelButtonText()
    }

    override fun addClickListeners() {
        super.addClickListeners()

        confirmBtn.setOnClickListener { onConfirmButtonClickCallback() }

        if (::onCancelButtonClickCallback.isInitialized) {
            cancelBtn.setOnClickListener { onCancelButtonClickCallback() }
        } else {
            cancelBtn.setOnClickListener { dismiss() }
        }
    }
}