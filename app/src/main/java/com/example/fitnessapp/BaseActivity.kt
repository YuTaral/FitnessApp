package com.example.fitnessapp

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.dialogs.BaseDialog
import com.example.fitnessapp.interfaces.INeedResumeDialog
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.managers.SharedPrefsManager


/** Base activity class to implement the common logic for all activities */
abstract class BaseActivity : AppCompatActivity()  {
    protected abstract var layoutId: Int

    /** List to store the currently active dialogs */
    var activeDialogs: MutableList<BaseDialog> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateActivity(this)
        setContentView(layoutId)

        if (this is MainActivity && AppStateManager.user == null && savedInstanceState != null) {
            // If this is the main activity, the user is not logged in and savedInstanceState is
            // non-null, app restart occurred, handle the case
            onAppRestart()
        }

        findViews()
        addClickListeners()

        // Enable animations
        val rootLayout = findViewById<ViewGroup>(R.id.activity_root)
        rootLayout.layoutTransition = LayoutTransition()
        rootLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    override fun onRestart() {
        super.onRestart()
        // Make sure the correct activity is set (error occur when in the following edge case:
        // Open camera from the app -> minimize the app -> return to the app -> close camera)
        updateActivity(this)
    }

    override fun onResume() {
        super.onResume()
        // Make sure the correct activity is set
        updateActivity(this)

        // Check for active dialog which need to execute resume logic
        for (dialog in activeDialogs) {
            if (dialog is INeedResumeDialog) {
                dialog.resume()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Make sure to clear the activity onPause(e.g app is backgrounded) - the AppStateManager
        // must not contain inactive activity
        updateActivity(null)
    }

    /** Find the views in the activity layout. Called by the base activity in onCreate */
    abstract fun findViews()

    /** Add click listeners to the views in the activity. Called by the base activity in onCreate */
    abstract fun addClickListeners()

    /** Execute the logic to reset the logged in user when app restart occurs */
    private fun onAppRestart() {
        val user = SharedPrefsManager.getStoredUser()

        if (user != null) {
            // If the currently logged in user is null, but the MainActivity is being recreated, it means
            // the app is being restarted (state lost, but activity not). In this case, set the user as
            // it is a must for the main activity to have logged in user.
            AppStateManager.user = user

        } else {
            // If something goes wrong, simply start the login activity, which will auto login
            // the user if there was logged in user
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    /** Update the activity in the AppStateManager */
    private fun updateActivity(value: BaseActivity?) {
        AppStateManager.activeActivity = value
    }
}