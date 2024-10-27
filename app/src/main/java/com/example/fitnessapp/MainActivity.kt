package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.fitnessapp.dialogs.SaveWorkoutTemplateDialog
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.panels.FragmentRefreshListener
import com.example.fitnessapp.panels.PanelPagerAdapter
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/** Main Activity class to hold the main logic of the application.
 * Displayed after successful login
 */
class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var actionsNavView: NavigationView
    private lateinit var profileIcon: ImageView
    private lateinit var menuIcon: ImageView
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        StateEngine.activeActivity = this

        // Find the views
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        actionsNavView = findViewById(R.id.action_nav_view)
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        profileIcon = findViewById(R.id.profile_img)
        menuIcon = findViewById(R.id.menu_img)

        // Set up the pager adapter
        initialisePager()

        // Initialise drawer logic - adds click listeners and overrides back button press
        initialiseDrawerLogic()
    }

    /** Sets the view pager */
    private fun initialisePager() {
        viewPager.adapter = PanelPagerAdapter(this, itemCount = 2)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = PanelPagerAdapter.Panel.entries[position].getTitle()
        }.attach()
        StateEngine.pager = viewPager
    }

    /** Add click listeners for selecting item from the drawer and back button pressed */
    private fun initialiseDrawerLogic() {
        profileIcon.setOnClickListener {
            findViewById<TextView>(R.id.txt_username).text = StateEngine.user.email
            drawerLayout.openDrawer(navView)
        }
        menuIcon.setOnClickListener{
            drawerLayout.openDrawer(actionsNavView)
        }

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

    /** Displays Workout panel
     * @param workout the workout model, updates the StateEngine variable
     * @param refreshWorkouts true if StateEngine variable to refresh workouts should be set to true,
     * false otherwise. If null provided, the variable is not being changed
     */
    fun displayWorkoutPanel(workout: WorkoutModel?, refreshWorkouts: Boolean?) {
        val index = PanelPagerAdapter.Panel.WORKOUT.getIndex()

        StateEngine.workout = workout

        if (refreshWorkouts != null) {
            StateEngine.refreshWorkouts = refreshWorkouts
        }

        if (index != StateEngine.pager.currentItem) {
            // If the index of the viewPager changes, this will trigger onResume(), which will re-populate
            // the panel
            StateEngine.pager.setCurrentItem(PanelPagerAdapter.Panel.WORKOUT.getIndex(), true)
        } else {
            // If the index of the viewPager does not chang, this will not trigger onResume(), notify the listener
            // to re-populate the panel
            val fragment = supportFragmentManager.findFragmentByTag("f$index") as FragmentRefreshListener
            fragment.onRefreshListener()
        }
    }
}