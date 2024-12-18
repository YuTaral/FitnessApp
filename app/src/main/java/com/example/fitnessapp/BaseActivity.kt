package com.example.fitnessapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.dialogs.BaseDialog
import com.example.fitnessapp.utils.AppStateManager


/** Base activity class to hold the common logic for all activities */
abstract class BaseActivity : AppCompatActivity()  {
    protected abstract var layoutId: Int

    /** List to store the currently active dialogs */
    var activeDialogs: MutableList<BaseDialog> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppStateManager.activeActivity = this
        setContentView(layoutId)

        findViews()
        addClickListeners()
    }

    /** Find the views in the activity layout. Called by the base activity in onCreate */
    abstract fun findViews()

    /** Add click listeners to the views in the activity. Called by the base activity in onCreate */
    abstract fun addClickListeners()
}