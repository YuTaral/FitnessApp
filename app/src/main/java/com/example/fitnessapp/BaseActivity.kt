package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.dialogs.BaseDialog
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Utils


/** Base activity class to hold the common logic for all activities */
abstract class BaseActivity : AppCompatActivity()  {
    protected abstract var layoutId: Int

    /** List to store the currently active dialogs */
    var activeDialogs: MutableList<BaseDialog> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppStateManager.activeActivity = this
        setContentView(layoutId)

        if (savedInstanceState != null && AppStateManager.user == null) {
            onAppRestart()
        }

        findViews()
        addClickListeners()
    }

    /** Find the views in the activity layout. Called by the base activity in onCreate */
    abstract fun findViews()

    /** Add click listeners to the views in the activity. Called by the base activity in onCreate */
    abstract fun addClickListeners()

    /** Execute the logic to reset the logged in user when app restart occurs */
    private fun onAppRestart() {
        val user = Utils.getStoredUser()

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
}