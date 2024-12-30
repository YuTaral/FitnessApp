package com.example.fitnessapp

import android.os.Bundle
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
import com.example.fitnessapp.dialogs.EditProfileDialog
import com.example.fitnessapp.dialogs.FinishWorkoutDialog
import com.example.fitnessapp.dialogs.SaveWorkoutTemplateDialog
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.panels.BaseExercisePanel
import com.example.fitnessapp.panels.ManageExercisesPanel
import com.example.fitnessapp.panels.ManageTeamsPanel
import com.example.fitnessapp.panels.TemplatesPanel
import com.example.fitnessapp.utils.ActivityResultHandler
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Utils
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/** Main Activity class to hold the main logic of the application.
 * Displayed after successful login
 */
class MainActivity : BaseActivity() {
    override var layoutId = R.layout.activity_main

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navProfileView: NavigationView
    private lateinit var navActionView: NavigationView
    private lateinit var profileIcon: ImageView
    private lateinit var menuIcon: ImageView
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    /** The ActivityResultHandler class instance to handle permissions / intents */
    lateinit var activityResultHandler: ActivityResultHandler

    /** The PanelAdapter class instance to manage the panels */
    lateinit var panelAdapter: PanelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the launchers
        activityResultHandler = ActivityResultHandler()
    }

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
            } else {
                navActionView.menu.findItem(R.id.nav_finish_workout).setVisible(false)
            }
            navActionView.menu.findItem(R.id.nav_save_workout_template).setVisible(true)
        }
    }

    /** Update the user image and name in the left drawer */
    fun setUserInDrawer() {
        if (!::navProfileView.isInitialized) {
            // Workaround to prevent this to throw error, in case the app was restarted - if it was
            // restarted the user is being set on BaseActivity.onAppRestart, which triggers setUserInDrawer
            // before findViews() is executed. Just return here, the setUserInDrawer is called later
            // in initialiseDrawerLogic which is fine to update the views in the drawer
            return
        }

        val profileImage = navProfileView.getHeaderView(0).findViewById<ImageView>(R.id.team_image)
        val username = navProfileView.getHeaderView(0).findViewById<TextView>(R.id.txt_username)

        // Set profile image if there is
        if (AppStateManager.user!!.profileImage.isNotEmpty()) {
            profileImage.setBackgroundResource(0)
            profileImage.setImageBitmap(Utils.convertStringToBitmap(AppStateManager.user!!.profileImage))
        } else {
            profileImage.setImageBitmap(null)
            profileImage.setBackgroundResource(R.drawable.icon_profile)
        }

        // Set username to email or full name
        if (AppStateManager.user!!.fullName.isEmpty()) {
            username.text = AppStateManager.user!!.email
        } else {
            username.text = AppStateManager.user!!.fullName
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

        panelAdapter = viewPager.adapter as PanelAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = panelAdapter.getPanelTitle(position)
        }.attach()
    }

    /** Add click listeners for selecting item from the drawer and back button pressed */
    private fun initialiseDrawerLogic() {
        profileIcon.setOnClickListener {
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

        // Set the user in the drawer
        setUserInDrawer()

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
                panelAdapter.displayTemporaryPanel(TemplatesPanel())
            }
            R.id.nav_manage_exercises -> {
                // Display the Muscle Groups and Exercises as temporary panel
                panelAdapter.displayTemporaryPanel(ManageExercisesPanel(BaseExercisePanel.Mode.SELECT_MUSCLE_GROUP))
            }
            R.id.nav_manage_teams -> {
                // Display the Manage Teams panel
                panelAdapter.displayTemporaryPanel(ManageTeamsPanel())
            }
        }
    }

    /** Execute the action based on the selected action in the left Drawer
     * @param menuItem the selected menu item
     * */
    private fun leftDrawerSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.exercise_default_values -> {
                // Fetch weight units and open the dialog
                WorkoutRepository().getWeightUnits(onSuccess = { weighUnits ->
                    DefaultValuesDialog(this, AppStateManager.user!!.defaultValues, weighUnits).show()
                })
            }
            R.id.edit_profile -> {
                // Open Edit profile dialog
                EditProfileDialog(this).show()
            }
            R.id.nav_change_pass -> {
                // Open change password dialog
                ChangePasswordDialog(this).show()
            }
            R.id.nav_logout -> {
                // Handle Log Out, ask for confirmation first
                val dialog = AskQuestionDialog(this, AskQuestionDialog.Question.LOG_OUT)

                dialog.setLeftButtonCallback {
                    UserRepository().logout(onSuccess = { Utils.onLogout() })
                }

                dialog.show()
            }
        }
    }
}