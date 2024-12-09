package com.example.fitnessapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnessapp.utils.StateEngine


/** Base activity class to hold the common logic for all activities */
abstract class BaseActivity : AppCompatActivity()  {
    protected abstract var layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateEngine.activeActivity = this
        setContentView(layoutId)

        findViews()
        addClickListeners()
    }

    /** Find the views in the activity layout. Called by the base activity in onCreate */
    abstract fun findViews()

    /** Add click listeners to the views in the activity. Called by the base activity in onCreate */
    abstract fun addClickListeners()
}