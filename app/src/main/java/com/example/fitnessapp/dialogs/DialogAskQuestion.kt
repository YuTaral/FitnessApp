package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.fitnessapp.R
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.WorkoutTemplateRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Dialog used to ask a question and execute a callback on confirm */
@SuppressLint("InflateParams")
class DialogAskQuestion(q: Question) {
    /** Enum to hold all questions */
    enum class Question(private val titleId: Int, private val questionId: Int) {
        DELETE_TEMPLATE(R.string.question_delete_template_title, R.string.question_delete_template_text);

        /** Returns the question title */
        fun getTitle(): String {
            return Utils.getActivity().getString(titleId)
        }

        /** Returns the question text */
        fun getQuestionText(): String {
            return Utils.getActivity().getString(questionId)
        }
    }

    private var question: Question
    private var dialogView: View
    private var title: TextView
    private var questionText: TextView
    private var closeIcon: ImageView
    private var confirmBtn: Button

    init {
        question = q
        dialogView = LayoutInflater.from(Utils.getContext()).inflate(R.layout.ask_question_dialog, null)

        // Find the views
        title = dialogView.findViewById(R.id.ask_question_title)
        closeIcon = dialogView.findViewById(R.id.dialog_close)
        questionText = dialogView.findViewById(R.id.question_lbl)
        confirmBtn = dialogView.findViewById(R.id.confirm_btn)
    }

    /** Shows the dialog
     * @param data any additional data used to populate the dialog
     */
    fun showDialog(data: Any) {
        val dialogBuilder = AlertDialog.Builder(Utils.getContext())
        dialogBuilder.setView(dialogView).setCancelable(false)
        val alertDialog = dialogBuilder.create()

        // Populate the data based on the question
        if (question == Question.DELETE_TEMPLATE) {
            val template: WorkoutModel = data as WorkoutModel

            title.text = question.getTitle()
            questionText.text = String.format(question.getQuestionText(), template.name)

            // Add confirm listener
            confirmBtn.setOnClickListener { deleteTemplate(template.id, alertDialog) }
        }

        // Close dialog listener
        closeIcon.setOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
    }

    /** Sends a request to delete the template
     * @param id the template id
     * @param alertDialog the alert dialog
     */
    private fun deleteTemplate(id: Long, alertDialog: AlertDialog) {
        WorkoutTemplateRepository().deleteWorkoutTemplate(id, onSuccess = {
            alertDialog.dismiss()
            StateEngine.panelAdapter.displayTemporaryPanel()
        })
    }
}