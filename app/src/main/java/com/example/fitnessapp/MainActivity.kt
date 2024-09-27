package com.example.fitnessapp

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.panels.MainPanel
import com.example.fitnessapp.panels.SelectedWorkoutPanel
import com.example.fitnessapp.utils.StateEngine

/** Main Activity class to hold the main logic of the application.
 * Displayed after successful login
 */
class MainActivity : ComponentActivity() {
    private lateinit var mainPanel: MainPanel
    private lateinit var selectedWorkoutPanel: SelectedWorkoutPanel
    private lateinit var mainPanelLbL: TextView
    private lateinit var newWorkoutPanelLbl: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        StateEngine.activeActivity = this

        // Find the views
        mainPanelLbL = findViewById(R.id.main_panel_lbl)
        newWorkoutPanelLbl = findViewById(R.id.panel_add_workout_lbl)

        // Create the two panels
        mainPanel = MainPanel(findViewById(R.id.main_panel))
        selectedWorkoutPanel = SelectedWorkoutPanel(findViewById(R.id.add_workout_panel))

        // Show the default panel
        displayMainPanel()

        // Add click listeners to display the different panels
        mainPanelLbL.setOnClickListener { displayMainPanel() }
        newWorkoutPanelLbl.setOnClickListener { displayNewWorkoutPanel() }
    }

    /** Handles Main panel clicked */
    private fun displayMainPanel() {
        findViewById<RelativeLayout>(R.id.add_workout_panel).visibility = View.GONE
        findViewById<View>(R.id.add_workout_panel_lbl_underline).visibility = View.GONE
        newWorkoutPanelLbl.setTypeface(null)

        findViewById<RelativeLayout>(R.id.main_panel).visibility = View.VISIBLE
        findViewById<View>(R.id.main_panel_lbl_underline).visibility = View.VISIBLE
        mainPanelLbL.setTypeface(null, Typeface.BOLD)

        if (StateEngine.refreshWorkouts) {
            mainPanel.populatePanel()
        }
    }

    /** Handles New Workout panel clicked */
    fun displayNewWorkoutPanel() {
        findViewById<RelativeLayout>(R.id.main_panel).visibility = View.GONE
        findViewById<View>(R.id.main_panel_lbl_underline).visibility = View.GONE

        mainPanelLbL.setTypeface(null)

        findViewById<RelativeLayout>(R.id.add_workout_panel).visibility = View.VISIBLE
        findViewById<View>(R.id.add_workout_panel_lbl_underline).visibility = View.VISIBLE
        newWorkoutPanelLbl.setTypeface(null, Typeface.BOLD)

        selectedWorkoutPanel.populatePanel()
    }

    /** Executes Select Workout */
    fun selectWorkout(workout: WorkoutModel) {
        NetworkManager.sendRequest(
            APIService.instance.getWorkout(workout.id),
            onSuccessCallback = { response ->
                StateEngine.workout = WorkoutModel(response.returnData[0])
                displayNewWorkoutPanel()
            }
        )
    }
}