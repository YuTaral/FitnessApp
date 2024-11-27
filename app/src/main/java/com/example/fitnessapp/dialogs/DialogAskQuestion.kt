package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.models.MGExerciseModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.utils.Utils

/** Dialog used to ask a question and execute a callback on confirm */
@SuppressLint("InflateParams")
class DialogAskQuestion(q: Question) {
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

    private var question: Question
    private lateinit var onYesCallback: () -> Unit
    private lateinit var onNoCallback: () -> Unit
    private var dialogView: View
    private var title: TextView
    private var questionText: TextView
    private var questionAdditionalInfo: TextView
    private var closeIcon: ImageView
    private var yesBtn: Button
    private var noBtn: Button
    private lateinit var alertDialog: AlertDialog

    init {
        question = q
        dialogView = LayoutInflater.from(Utils.getContext()).inflate(R.layout.ask_question_dialog, null)

        // Find the views
        title = dialogView.findViewById(R.id.ask_question_title)
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        questionText = dialogView.findViewById(R.id.question_lbl)
        questionAdditionalInfo = dialogView.findViewById(R.id.question_additional_info)
        yesBtn = dialogView.findViewById(R.id.yes_btn)
        noBtn = dialogView.findViewById(R.id.no_btn)
    }

    /** Setter for the callback which will be executed on confirm button click */
    fun setYesCallback(callback: () -> Unit) {
        onYesCallback = callback
    }

    /** Setter for the callback which will be executed on cancel button click */
    fun setNoCallback(callback: () -> Unit) {
        onNoCallback = callback
    }

    /** Shows the dialog
     * @param data any additional data used to populate the dialog
     */
    fun showDialog(data: Any) {
        var formatName = ""
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
        dialogBuilder.setView(dialogView).setCancelable(false)
        alertDialog = dialogBuilder.create()

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

        // Add click listeners
        yesBtn.setOnClickListener { onYesCallback() }

        if (::onNoCallback.isInitialized) {
            noBtn.setOnClickListener { onNoCallback() }
        } else {
            noBtn.setOnClickListener { alertDialog.dismiss() }
        }

        closeIcon.setOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
    }

    /** Closes the alert dialog */
    fun closeDialog() {
        alertDialog.dismiss()
    }
}