package com.example.fitnessapp.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import com.example.fitnessapp.BaseActivity
import com.example.fitnessapp.LoginActivity
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.R
import com.example.fitnessapp.adapters.PanelAdapter
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.managers.PermissionResultManager
import com.example.fitnessapp.managers.VibratorWarningManager
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Object with common methods */
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
        showMessage(getActivity().getText(msgId).toString(), duration)
    }

    /** Show a snack-bar message
     * @param message the message to show
     * @param duration duration - short / long, long by default
     */
    fun showMessage(message: String, duration: Int = BaseTransientBottomBar.LENGTH_LONG) {
        val snackBarContainer = Snackbar.make(getActivity().findViewById(R.id.user_message), message, duration)
        val textView = snackBarContainer.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

        // Customizing position for top positioning
        val layoutParams = snackBarContainer.view.layoutParams as FrameLayout.LayoutParams
        layoutParams.gravity = Gravity.TOP
        snackBarContainer.view.layoutParams = layoutParams
        snackBarContainer.animationMode = Snackbar.ANIMATION_MODE_FADE

        textView.maxLines = 4
        textView.textSize = 18f
        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER

        snackBarContainer.setBackgroundTint(getActivity().getColor(R.color.colorAccent))
        snackBarContainer.show()
    }

    /** Show a toast message
     * @param msgId the message id
     * @param duration duration - short / long, long by default
     */
    fun showToast(msgId: Int, duration: Int = BaseTransientBottomBar.LENGTH_LONG) {
        showToast(getActivity().getText(msgId).toString(), duration)
    }

    /** Show a toast message
    * @param message the message to show
    * @param duration duration - short / long, long by default
    * */
    @SuppressLint("InflateParams")
    @Suppress("DEPRECATION")
    fun showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
        // Create a custom layout for the Toast
        val layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_custom_layout, null)

        val textView: TextView = layout.findViewById(R.id.toast_text)
        textView.text = message

        val toast = Toast(getActivity())
        toast.duration = duration
        toast.view = layout

        // Set the Toast position at the top of the screen
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100) // Adjust the vertical margin as needed

        // Add animation
        layout.alpha = 0f
        layout.animate()
            .alpha(1f)
            .setDuration(1000)
            .withEndAction {
                layout.animate()
                    .alpha(0f)
                    .setDuration(1000)
                    .setStartDelay(3000)
                    .start()
            }

        toast.show()
    }

    /** Check whether the value is ResponseCode.SUCCESS
     * @param value the value to check
     */
    fun isSuccessResponse(value:Int ): Boolean {
        return value == Constants.ResponseCode.SUCCESS.ordinal
    }

    /** Check whether the value is ResponseCode.TOKEN_EXPIRED
     * @param value the value to check
     */
    fun istTokenExpiredResponse(value:Int ): Boolean {
        return value == Constants.ResponseCode.TOKEN_EXPIRED.ordinal
    }

    /** Check whether the value is ResponseCode.REFRESH_TOKEN
     * @param value the value to check
     */
    fun isTokenRefreshResponse(value:Int ): Boolean {
        return value == Constants.ResponseCode.REFRESH_TOKEN.ordinal
    }

    /** Check whether the value is ResponseCode.Fail or ResponseCode.UNEXPECTED_ERROR
     * @param value the value to check
     */
    fun isFail(value:Int ): Boolean {
        return value != Constants.ResponseCode.SUCCESS.ordinal
    }

    /** Return the current activity */
    fun getActivity(): BaseActivity {
        return AppStateManager.activeActivity!!
    }

    /** Return true of the application is minimized, false if not */
    fun isAppMinimized(): Boolean {
        return AppStateManager.activeActivity == null
    }
    /** Return the current panel adapter */
    fun getPanelAdapter(): PanelAdapter {
        return (AppStateManager.activeActivity!! as MainActivity).panelAdapter
    }

    /** Return the current activity result handler */
    fun getActivityResultHandler(): PermissionResultManager {
        return (AppStateManager.activeActivity!! as MainActivity).perrmissionHandler
    }

    /** Validation failed - focus the field and open the keyboard
     * @param input the invalid input view
     * @param errorMsgId the id of the error message
     */
    fun validationFailed(input: EditText, errorMsgId: Int) {
        openKeyboardOnInput(input)
        showToast(errorMsgId)
        VibratorWarningManager.makeVibration(getActivity(), longArrayOf(0, 350))
    }

    /** Focus the field and open the keyboard
     * @param input the input view
     */
    fun openKeyboardOnInput(input: EditText) {
        val imm = getActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // Request focus and open keyboard
        input.requestFocus()
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT)
    }

    /** Close the keyboard
     * @param view the focused view
     * */
    fun closeKeyboard(view: View) {
        val imm = getActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

    /** Convert the date to default app format date and time - dd/MMM/yyyy
     * @param date the date to format
     */
    fun defaultFormatDateTime(date: Date): String {
        return SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US).format(date)
    }

    /** Convert the string to date
     * @param dateString the date as string
     */
    fun parseDateTime(dateString: String): Date? {
        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US)
        return try {
            dateFormat.parse(dateString)
        } catch (e: ParseException) {
            // Handle the error if parsing fails
            e.printStackTrace()
            null
        }
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


    /** Execute the callback when user logout */
    fun onLogout() {
        // Update the service by removing the token
        APIService.updateToken("")

        // Update the State engine
        AppStateManager.user = null
        AppStateManager.workout = null

        // Start the logic activity
        val intent = Intent(getActivity(), LoginActivity::class.java)
        getActivity().startActivity(intent)
        getActivity().finish()
    }

    /** The click listener for Add Exercise and Edit Exercise Buttons
     * @param question the question to ask if the current workout is already finished
     * @param callback the callback to execute
     */
    fun addEditExerciseClick(question: AskQuestionDialog.Question, callback: () -> Unit) {
        if (AppStateManager.workout!!.finishDateTime != null) {
            // Workout finished, ask the user whether to remove the finished time
            val dialog = AskQuestionDialog(getActivity(), question)

            dialog.setConfirmButtonCallback {
                val unFinishedWorkout = AppStateManager.workout!!
                unFinishedWorkout.finishDateTime = null
                unFinishedWorkout.durationSeconds = 0

                WorkoutRepository().updateWorkout(unFinishedWorkout, onSuccess = { workout ->
                    // Refresh the workout panel
                    getPanelAdapter().refreshWorkoutPanel(workout, true)

                    // Close the ask question dialog
                    dialog.dismiss()

                    // Execute the callback
                    callback()
                })
            }

            dialog.show()

        } else {
            // Execute the callback without asking question
            callback()
        }
    }

    /** Return the image content as encoded Base64 string or empty string if image drawable is empty
     * @param image the image
     */
    fun encodeImageToString(image: ImageView): String {
        val outputStream = ByteArrayOutputStream()

        val bitmap = try {
            image.drawable.toBitmap()
        } catch (e: Exception) {
            return "" // Image drawable is empty, return empty string
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    /** Return bitmap from the provided string
     * @param image the image sa Base64 string
     */
    fun convertStringToBitmap(image: String): Bitmap {
        val imageBytes = Base64.decode(image, Base64.DEFAULT)

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}