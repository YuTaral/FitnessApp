package com.example.fitnessapp

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import com.example.fitnessapp.dialogs.SaveWorkoutTemplateDialog
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.panels.MainPanel
import com.example.fitnessapp.panels.SelectedWorkoutPanel
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils
import com.google.android.material.navigation.NavigationView

/** Main Activity class to hold the main logic of the application.
 * Displayed after successful login
 */
class MainActivity : ComponentActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var actionsNavView: NavigationView
    private lateinit var mainPanel: MainPanel
    private lateinit var selectedWorkoutPanel: SelectedWorkoutPanel
    private lateinit var mainPanelLbL: TextView
    private lateinit var newWorkoutPanelLbl: TextView
    private lateinit var profileIcon: ImageView
    private lateinit var menuIcon: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        StateEngine.activeActivity = this

        // Find the views
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        actionsNavView = findViewById(R.id.action_nav_view)
        mainPanelLbL = findViewById(R.id.main_panel_lbl)
        newWorkoutPanelLbl = findViewById(R.id.panel_add_workout_lbl)
        profileIcon = findViewById(R.id.profile_img)
        menuIcon = findViewById(R.id.menu_img)

        // Create the two panels
        mainPanel = MainPanel(findViewById(R.id.main_panel))
        selectedWorkoutPanel = SelectedWorkoutPanel(findViewById(R.id.add_workout_panel))

        // Show the default panel
        displayMainPanel()

        // Add click listeners to display the different panels
        mainPanelLbL.setOnClickListener { displayMainPanel() }
        newWorkoutPanelLbl.setOnClickListener { displayNewWorkoutPanel() }

        // Add click listener to display the Navigations
        profileIcon.setOnClickListener {
            findViewById<TextView>(R.id.txt_username).text = StateEngine.user.email
            drawerLayout.openDrawer(navView)
        }
        menuIcon.setOnClickListener{
            drawerLayout.openDrawer(actionsNavView)
        }

        // Initialise drawer logic - adds click listener for menu item selection
        // and overrides back button press
        initialiseDrawerLogic()
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

    /** Add click listeners for selecting item from the drawer and back button pressed */
    private fun initialiseDrawerLogic() {
        navView.setNavigationItemSelectedListener { menuItem ->
            leftDrawerSelected(menuItem)
            drawerLayout.closeDrawers()
            true
        }
        actionsNavView.setNavigationItemSelectedListener { menuItem ->
            rightDrawerSelected(menuItem)
            drawerLayout.closeDrawers()
            true
        }

        // Close drawer on back button press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(navView)) {
                    drawerLayout.closeDrawer(navView)

                } else if (drawerLayout.isDrawerOpen(actionsNavView)) {
                    drawerLayout.closeDrawer(actionsNavView)

                } else {
                    // Pass the back press event to the system default handler
                    isEnabled = false // Disable this callback
                    onBackPressedDispatcher.onBackPressed() // Call the system's back press
                }
            }
        })
    }

    /** Executes the action based on the selected action in the right Drawer
     * @param menuItem the selected menu item
     * */
    private fun rightDrawerSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_save_workout_template -> {
                if (StateEngine.workout == null) {
                    Utils.showToast(R.string.error_msg_no_workout_selected)
                    return
                }

                SaveWorkoutTemplateDialog().showDialog()
            }
        }
    }

    /** Executes the action based on the selected action in the left Drawer
     * @param menuItem the selected menu item
     * */
    private fun leftDrawerSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_logout -> {
                // Handle Logout
                UserRepository().logout(onSuccess = {
                    StateEngine.workout = null
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}