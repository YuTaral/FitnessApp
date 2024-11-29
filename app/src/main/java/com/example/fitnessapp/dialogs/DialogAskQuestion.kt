package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.models.BaseModel
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.utils.Utils

/** Dialog used to ask a question and execute a callback on confirm */
@SuppressLint("InflateParams")
class DialogAskQuestion(ctx: Context, q: Question, d: BaseModel): BaseAlertDialog(ctx) {
    /** Enum to hold all questions */
    enum class Question(private val titleId: Int, private val questionId: Int,
                        private val yesBtnTextId: Int, private val noBtnTextId: Int) {

        DELETE_TEMPLATE(R.string.question_delete_template_title, R.string.question_delete_template_text, R.string.yes_btn, R.string.no_btn),
        DELETE_MG_EXERCISE(R.string.question_delete_exercise_title, R.string.question_delete_exercise_text, R.string.yes_btn, R.string.no_btn),
        OVERRIDE_EXISTING_EXERCISE(R.string.question_override_exercise_title, R.string.question_override_exercise_text, R.string.override_btn, R.string.create_new_btn);

        /** Returns the question title */
        fun getTitle(): String {
            return Utils.getActivity().getString(titleId)
        }

        /** Returns the question text */
        fun getQuestionText(): String {
            return Utils.getActivity().getString(questionId)
        }

        /** Returns the Yes button text */
        fun getYesText(): String {
            return Utils.getActivity().getString(yesBtnTextId)
        }

        /** Returns the No button text */
        fun getNoText(): String {
            return Utils.getActivity().getString(noBtnTextId)
        }
    }

    override var layoutId = R.layout.ask_question_dialog

    private var question = q
    private var data = d

    private lateinit var onYesCallback: () -> Unit
    private lateinit var onNoCallback: () -> Unit
    private lateinit var title: TextView
    private lateinit var questionText: TextView
    private lateinit var questionAdditionalInfo: TextView
    private lateinit var yesBtn: Button
    private lateinit var noBtn: Button
    private lateinit var alertDialog: AlertDialog

    /** Setter for the callback which will be executed on confirm button click */
    fun setYesCallback(callback: () -> Unit) {
        onYesCallback = callback
    }

    /** Setter for the callback which will be executed on cancel button click */
    fun setNoCallback(callback: () -> Unit) {
        onNoCallback = callback
    }

    override fun findViews() {
        title = dialogView.findViewById(R.id.ask_question_title)
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
                questionAdditionalInfo.text = String.format(Utils.getContext().getString(R.string.mg_exercise_description), model.description)
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
            noBtn.setOnClickListener { alertDialog.dismiss() }
        }
    }
}