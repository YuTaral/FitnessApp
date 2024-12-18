package com.example.fitnessapp.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.fitnessapp.LoginActivity
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.R
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.models.UserModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Object to hold common methods */
object Utils {
    private const val SECURE_PREFS_FILE_NAME = "secure_prefs"
    private const val AUTH_TOKEN_KEY = "auth_token"
    private const val SERIALIZED_USER_KEY = "serialized_user"
    private const val IMAGE_WIDTH = 256
    private const val IMAGE_HEIGHT = 256

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
        val snackBarContainer = Snackbar.make(AppStateManager.activeActivity.findViewById(R.id.user_message), message, duration)

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
        return value == Constants.ResponseCode.FAIL.ordinal ||
               value == Constants.ResponseCode.UNEXPECTED_ERROR.ordinal
    }

    /** Return the current activity when context is needed */
    fun getContext(): Context {
        return AppStateManager.activeActivity
    }

    /** Return the current activity */
    fun getMainActivity(): MainActivity {
        return AppStateManager.activeActivity as MainActivity
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

    /** Return authorization token stored in the shared prefs, if it does not exist return empty string */
    fun getStoredToken(): String {
        return getSharedPref().getString(AUTH_TOKEN_KEY, null) ?: return ""
    }

    /** Return User model stored in the shared prefs, if it does not exist return null */
    fun getStoredUser(): UserModel? {
        val sharedPref = getSharedPref()
        val serializedUser = sharedPref.getString(SERIALIZED_USER_KEY, null) ?: ""

        if (serializedUser.isEmpty())
        {
            return null
        }

        return UserModel(serializedUser)
    }

    /** Save / Remove the authorization token in shared prefs
     * @param token the token if we need to save it, empty string if we need to remove it
     */
    fun updateTokenInPrefs(token: String) {
        val sharedPref = getSharedPref()

        if (token.isEmpty()) {
            sharedPref.edit().remove(AUTH_TOKEN_KEY).apply()
        } else {
            sharedPref.edit().putString(AUTH_TOKEN_KEY, token).apply()
        }
    }

    /** Save / Remove serialized user model in shared prefs
     * @param model the user model if we must save the user, null if we need to remove it
     */
    fun updateUserInPrefs(model: UserModel?) {
        val sharedPref = getSharedPref()

        if (model == null) {
            sharedPref.edit().remove(SERIALIZED_USER_KEY).apply()
        } else {
            sharedPref.edit().putString(SERIALIZED_USER_KEY, serializeObject(model)).apply()
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
        val intent = Intent(getMainActivity(), LoginActivity::class.java)
        getMainActivity().startActivity(intent)
        getMainActivity().finish()
    }

    /** Log the error */
    fun logException(exception: Exception) {
        Log.e(ContentValues.TAG, "Error: ${exception.message}", exception)
    }

    /** The click listener for Add Exercise and Edit Exercise Buttons
     * @param question the question to ask if the current workout is already finished
     * @param callback the callback to execute
     */
    fun addEditExerciseClick(question: AskQuestionDialog.Question, callback: () -> Unit) {
        if (AppStateManager.workout!!.finishDateTime != null) {
            // Workout finished, ask the user whether to remove the finished time
            val dialog = AskQuestionDialog(getContext(), question)

            dialog.setYesCallback {
                val unFinishedWorkout = AppStateManager.workout!!
                unFinishedWorkout.finishDateTime = null
                unFinishedWorkout.durationSeconds = 0

                WorkoutRepository().editWorkout(unFinishedWorkout, onSuccess = { workout ->
                    // Refresh the workout panel
                    AppStateManager.panelAdapter.displayWorkoutPanel(workout, true)

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

    /**
     * Scales a bitmap to fit within the specified width and height while maintaining aspect ratio.
     * Return the bitmap is success, null otherwise
     * @param bitmap the image bitmap
     */
    fun scaleBitmap(bitmap: Bitmap): Bitmap? {
        try {
            val width = bitmap.width
            val height = bitmap.height

            // Calculate the scaling factor while maintaining the aspect ratio
            val scaleFactor = minOf(IMAGE_WIDTH.toFloat() / width, IMAGE_HEIGHT.toFloat() / height)

            return if (scaleFactor < 1) {
                val matrix = Matrix()
                matrix.postScale(scaleFactor, scaleFactor)

                // Create a new scaled bitmap
                Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
            } else {
                // If the bitmap is already small, return it as is
                bitmap
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Create and scales a bitmap from the provided uri to fit within the specified width and height
     * while maintaining aspect ratio. Return the bitmap is success, null otherwise
     * @param uri the image uri
     */
    fun scaleBitmap(uri: Uri): Bitmap? {
        try {
            val contentResolver = getMainActivity().contentResolver

            val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // For API 28+ (Pie and above), use ImageDecoder with scaling
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.setTargetSampleSize(4)
                }

            } else {
                // For older APIs, scale down using BitmapFactory
                val inputStream: InputStream? = contentResolver.openInputStream(uri)

                // Decode only the image dimensions first
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()

                // Calculate the appropriate sample size
                options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight)
                options.inJustDecodeBounds = false

                // Decode the scaled-down bitmap
                val scaledInputStream: InputStream? = contentResolver.openInputStream(uri)
                val scaledBitmap = BitmapFactory.decodeStream(scaledInputStream, null, options)
                scaledInputStream?.close()
                scaledBitmap!!
            }

            return bitmap

        } catch (e: Exception) {
            e.printStackTrace()
            return null
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

    /** Calculate a sample size to scale down the image.
     * @param rawWidth the actual image width
     * @param rawHeight the actual image height
     */
    private fun calculateInSampleSize(rawWidth: Int, rawHeight: Int): Int {
        var inSampleSize = 1

        if (rawHeight > IMAGE_HEIGHT || rawWidth > IMAGE_WIDTH) {
            val halfHeight = rawHeight / 2
            val halfWidth = rawWidth / 2

            while ((halfHeight / inSampleSize) >= IMAGE_HEIGHT && (halfWidth / inSampleSize) >= IMAGE_WIDTH) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /** Create and return SharedPreferences object using encryption */
    private fun getSharedPref(): SharedPreferences {
        val masterKey = MasterKey.Builder(getContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            getContext(),
            SECURE_PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}