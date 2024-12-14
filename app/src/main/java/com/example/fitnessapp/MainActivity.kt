package com.example.fitnessapp

import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.fitnessapp.adapters.PanelAdapter
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.dialogs.ChangePasswordDialog
import com.example.fitnessapp.dialogs.DefaultValuesDialog
import com.example.fitnessapp.dialogs.FinishWorkoutDialog
import com.example.fitnessapp.dialogs.SaveWorkoutTemplateDialog
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.panels.BaseExercisePanel
import com.example.fitnessapp.panels.ManageExercisesPanel
import com.example.fitnessapp.panels.TemplatesPanel
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Utils
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/** Main Activity class to hold the main logic of the application.
 * Displayed after successful login
 */
class MainActivity : BaseActivity() {
    override var layoutId = R.layout.main_activity

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navProfileView: NavigationView
    private lateinit var navActionView: NavigationView
    private lateinit var profileIcon: ImageView
    private lateinit var menuIcon: ImageView
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun findViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navProfileView = findViewById(R.id.nav_profile_view)
        navActionView = findViewById(R.id.nav_action_view)
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        profileIcon = findViewById(R.id.profile_img)
        menuIcon = findViewById(R.id.menu_img)
    }

    override fun addClickListeners() {
        initialisePager()
        initialiseDrawerLogic()
    }

    /** Update the selected actions in the right drawer based on whether there is selected workout */
    fun updateActions() {
        if (AppStateManager.workout == null) {
            // Hide the workout related actions
            navActionView.menu.findItem(R.id.nav_finish_workout).setVisible(false)
            navActionView.menu.findItem(R.id.nav_save_workout_template).setVisible(false)
        } else {
            // Display the workout related actions
            if (AppStateManager.workout!!.finishDateTime == null) {
                navActionView.menu.findItem(R.id.nav_finish_workout).setVisible(true)
            }
            navActionView.menu.findItem(R.id.nav_save_workout_template).setVisible(true)
        }
    }

    /** Set the view pager */
    private fun initialisePager() {
        // Set offscreenPageLimit to ensure all non temporary panels are created upon initialization
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = PanelAdapter(viewPager, this, viewPager.offscreenPageLimit)

        // Remove the temporary Templates panel on page selected
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                (viewPager.adapter as PanelAdapter).onPanelSelectionChange(position)
            }
        })

        AppStateManager.panelAdapter = viewPager.adapter as PanelAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = AppStateManager.panelAdapter.getPanelTitle(position)
        }.attach()
    }

    /** Add click listeners for selecting item from the drawer and back button pressed */
    private fun initialiseDrawerLogic() {
        profileIcon.setOnClickListener {
            findViewById<TextView>(R.id.txt_username).text = AppStateManager.user!!.email
            drawerLayout.openDrawer(navProfileView)
        }

        menuIcon.setOnClickListener {
            drawerLayout.openDrawer(navActionView)
        }

        navProfileView.setNavigationItemSelectedListener { menuItem ->
            leftDrawerSelected(menuItem)
            drawerLayout.closeDrawers()
            true
        }

        navActionView.setNavigationItemSelectedListener { menuItem ->
            rightDrawerSelected(menuItem)
            drawerLayout.closeDrawers()
            true
        }

        // Update the actions in the right drawer
        updateActions()

        // Close drawer on back button press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(navProfileView)) {
                    drawerLayout.closeDrawer(navProfileView)

                } else if (drawerLayout.isDrawerOpen(navActionView)) {
                    drawerLayout.closeDrawer(navActionView)

                } else {
                    // Pass the back press event to the system default handler
                    isEnabled = false // Disable this callback
                    onBackPressedDispatcher.onBackPressed() // Call the system's back press
                }
            }
        })
    }

    /** Execute the action based on the selected action in the right Drawer
     * @param menuItem the selected menu item
     * */
    private fun rightDrawerSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_finish_workout -> {
                // Open finish workout dialog
                FinishWorkoutDialog(this).show()
            }
            R.id.nav_save_workout_template -> {
                // Open the dialog to save the workout as template
                SaveWorkoutTemplateDialog(this).show()
            }
            R.id.nav_manage_templates -> {
                // Display the Templates as temporary panel
                AppStateManager.panelAdapter.displayTemporaryPanel(TemplatesPanel())
            }
            R.id.nav_manage_exercises -> {
                // Display the Muscle Groups and Exercises as temporary panel
                AppStateManager.panelAdapter
                    .displayTemporaryPanel(ManageExercisesPanel(BaseExercisePanel.Mode.SELECT_MUSCLE_GROUP))
            }
        }
    }

    /** Execute the action based on the selected action in the left Drawer
     * @param menuItem the selected menu item
     * */
    private fun leftDrawerSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.exercise_default_values -> {
                WorkoutRepository().getWeightUnits(onSuccess = { weighUnits ->
                    DefaultValuesDialog(this, AppStateManager.user!!.defaultValues, weighUnits).show()
                })
            }
            R.id.nav_change_pass -> {
                // Open change password dialog
                ChangePasswordDialog(this).show()
            }
            R.id.nav_logout -> {
                // Handle Log Out, ask for confirmation first
                val dialog = AskQuestionDialog(this, AskQuestionDialog.Question.LOG_OUT)

                dialog.setYesCallback {
                    UserRepository().logout(onSuccess = { Utils.onLogout() })
                }

                dialog.show()
            }
        }
    }
}