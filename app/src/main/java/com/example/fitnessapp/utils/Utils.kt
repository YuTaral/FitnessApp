package com.example.fitnessapp.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.R
import com.example.fitnessapp.models.ExerciseModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Object to hold common methods */
object Utils {

    /** Email validation
     * @param target the email to check
     */
    fun isValidEmail(target: CharSequence?): Boolean {
        return !target.isNullOrBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }

    /** Show a snack-bar message
     * @param msgId the message id
     * @param duration duration - short / long, long by default
     */
    fun showMessage(msgId: Int, duration: Int = BaseTransientBottomBar.LENGTH_LONG) {
        showMessage(getContext().getText(msgId).toString(), duration)
    }

    /** Show a snack-bar message
     * @param message the message to show
     * @param duration duration - short / long, long by default
     */
    fun showMessage(message: String, duration: Int = BaseTransientBottomBar.LENGTH_LONG) {
        Snackbar.make(StateEngine.activeActivity.findViewById(R.id.user_message), message, duration)
                .setBackgroundTint(getContext().getColor(R.color.snackbarGreyColor))
                .setTextMaxLines(3)
                .show()
    }

    /** Show a toast message
     * @param msgId the message id
     * @param duration duration - short / long, long by default
     */
    fun showToast(msgId: Int, duration: Int = BaseTransientBottomBar.LENGTH_LONG) {
        showToast(getContext().getText(msgId).toString(), duration)
    }

    /** Show a snack-bar message
    * @param message the message to show
    * @param duration duration - short / long, long by default
    * */
    fun showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(getContext(), message, duration).show()
    }

    /** Called when network error occurred when processing request
     * @param t error
     */
    fun onNetworkFailure(t: Throwable) {
        // Show the error
        if (t.message.toString().isEmpty()) {
            showMessage(R.string.error_msg_unexpected)
        } else {
           showMessage(t.message.toString())
        }
    }

    /** Checks whether the value is ResponseCode.Success
     * @param value the value to check
     */
    fun isSuccessRespCode(value:Int ): Boolean {
        return value == Constants.ResponseCode.SUCCESS.ordinal
    }

    /** Returns the current activity when context is needed */
    fun getContext(): Context {
        return StateEngine.activeActivity
    }

    /** Returns the current activity*/
    fun getActivity(): MainActivity {
        return StateEngine.activeActivity as MainActivity
    }

    /** Validate the data in the dialog when save is clicked
     * @param name the name input view
     * @param sets the set input view
     * @param reps the reps input view
     * @param weight the weight input view
     */
     fun validateExercise(name: EditText, sets: EditText, reps: EditText, weight: EditText): ExerciseModel? {
        val exerciseName = name.text.toString()
        var exerciseSets = 0
        var setReps = 0
        var exerciseWeight = 0.0

        if (sets.text.toString().isNotEmpty()) {
            exerciseSets = sets.text.toString().toInt()
        }
        if (reps.text.toString().isNotEmpty()) {
            setReps = reps.text.toString().toInt()
        }
        if (weight.text.toString().isNotEmpty()) {
            exerciseWeight = weight.text.toString().toDouble()
        }

        // Validate Name
        if (exerciseName.isEmpty()) {
            validationFailed(name, R.string.error_msg_enter_ex_name)
            return null
        }

        // Validation passed
        return ExerciseModel(exerciseName, exerciseSets, setReps, exerciseWeight)
    }

    /** Validation failed - focus the field and open the keyboard
     * @param input the invalid input view
     * @param errorMsgId the id of the error message
     */
    fun validationFailed(input: EditText, errorMsgId: Int) {
        openKeyboardOnInput(input)
        showToast(errorMsgId)
    }

    /** Focuses the field and opens the keyboard
     * @param input the input view
     */
    fun openKeyboardOnInput(input: EditText) {
        val imm = getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Request focus and open keyboard
        input.requestFocus()
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
    }

    /** Closes the keyboard
     * @param view the focused view
     * */
    fun closeKeyboard(view: View) {
        val imm = getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /** JSON serializes an object using Gson and returns it
     * @param obj the object to serialize
     */
    fun serializeObject(obj: Any): String {
        val gson = Gson()
        return gson.toJson(obj)
    }

    /** Converts the date to default app format date - dd/MMM/yyyy
     * @param date the date to format
     */
    fun defaultFormatDate(date: Date): String {
        return SimpleDateFormat("dd/MMM/yyyy", Locale.US).format(date)
    }
}