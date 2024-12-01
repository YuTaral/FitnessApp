package com.example.fitnessapp.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.R
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
        val snackBarContainer = Snackbar.make(StateEngine.activeActivity.findViewById(R.id.user_message), message, duration)

        val textView = snackBarContainer.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.maxLines = 4
        textView.textSize = 18f

        snackBarContainer.setBackgroundTint(getContext().getColor(R.color.colorAccent))
        snackBarContainer.show()
    }

    /** Show a toast message
     * @param msgId the message id
     * @param duration duration - short / long, long by default
     */
    fun showToast(msgId: Int, duration: Int = BaseTransientBottomBar.LENGTH_LONG) {
        showToast(getContext().getText(msgId).toString(), duration)
    }

    /** Show a toast message
    * @param message the message to show
    * @param duration duration - short / long, long by default
    * */
    fun showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(getContext(), message, duration).show()
    }

    /** Checks whether the value is ResponseCode.Success
     * @param value the value to check
     */
    fun isSuccessRespCode(value:Int ): Boolean {
        return value == Constants.ResponseCode.SUCCESS.ordinal
    }

    /** Return the current activity when context is needed */
    fun getContext(): Context {
        return StateEngine.activeActivity
    }

    /** Return the current activity */
    fun getActivity(): MainActivity {
        return StateEngine.activeActivity as MainActivity
    }

    /** Validation failed - focus the field and open the keyboard
     * @param input the invalid input view
     * @param errorMsgId the id of the error message
     */
    fun validationFailed(input: EditText, errorMsgId: Int) {
        openKeyboardOnInput(input)
        showToast(errorMsgId)
    }

    /** Focus the field and open the keyboard
     * @param input the input view
     */
    fun openKeyboardOnInput(input: EditText) {
        val imm = getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Request focus and open keyboard
        input.requestFocus()
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
    }

    /** Close the keyboard
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

    /** Convert the date to default app format date - dd/MMM/yyyy
     * @param date the date to format
     */
    fun defaultFormatDate(date: Date): String {
        return SimpleDateFormat("dd MMM yyyy", Locale.US).format(date)
    }

    /** Used to smoothly expand view
     * @param view the view to expand
     */
    fun expandView(view: View) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val targetHeight = view.measuredHeight

        // Set the height to 0 and make it visible
        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            view.layoutParams.height = animatedValue
            view.requestLayout()
        }
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 350
        animator.start()
    }

    /** Used to smoothly collapse view
     * @param view the view to collapse
     */
    fun collapseView(view: View) {
        val initialHeight = view.measuredHeight

        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Int
            view.layoutParams.height = animatedValue
            view.requestLayout()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.visibility = View.GONE
            }
        })
        animator.interpolator = DecelerateInterpolator()
        animator.duration = 350
        animator.start()
    }

    /** Used to smoothly hide the view with fade
     * @param view the view to hide
     */
    fun hideViewWithFade(view: View) {
        view.animate().translationX(-view.width.toFloat()).alpha(0f).setDuration(350)
            .withEndAction {
                view.visibility = View.GONE
                view.translationX = 0f
                view.alpha = 1f
            }.start()
    }

    /** Used to smoothly display the view with fade
     * @param view the view to display
     */
    fun displayViewWithFade(view: View) {
        view.translationX = view.width.toFloat()
        view.alpha = 0f
        view.visibility = View.VISIBLE

        view.animate().translationX(0f).alpha(1f).setDuration(350).start()
    }

    /** Format a double value depending on the value after the decimal point.
     * @param value the value
     */
    fun formatDouble(value: Double): String {
        return if (value % 1 == 0.0) {
            String.format("%.0f", value)
        } else {
            String.format("%.3f", value)
        }
    }
}