package com.example.fitnessapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.fitnessapp.adapters.PanelAdapter
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.dialogs.ChangePasswordDialog
import com.example.fitnessapp.dialogs.DefaultValuesDialog
import com.example.fitnessapp.dialogs.EditProfileDialog
import com.example.fitnessapp.dialogs.FinishWorkoutDialog
import com.example.fitnessapp.dialogs.SaveWorkoutTemplateDialog
import com.example.fitnessapp.dialogs.SelectTimerDurationDialog
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.managers.PermissionResultManager
import com.example.fitnessapp.network.repositories.NotificationRepository
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.network.repositories.WorkoutRepository
import com.example.fitnessapp.panels.BaseExercisePanel
import com.example.fitnessapp.panels.ManageExercisesPanel
import com.example.fitnessapp.panels.ManageTeamsPanel
import com.example.fitnessapp.panels.ManageTemplatesPanel
import com.example.fitnessapp.panels.NotificationsPanel
import com.example.fitnessapp.utils.Utils
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/** Main Activity class to implement the main logic of the application.
 * Displayed after successful login
 */
class MainActivity : BaseActivity() {
    override var layoutId = R.layout.activity_main

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navProfileView: NavigationView
    private lateinit var navActionView: NavigationView
    private lateinit var profileIcon: ImageView
    private lateinit var accountLbl: TextView
    private lateinit var notificationIcon: ImageView
    private lateinit var notificationsLbl: TextView
    private lateinit var activeNotificationIcon: ImageView
    private lateinit var menuIcon: ImageView
    private lateinit var moreLbl: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    /** The ActivityResultHandler class instance to handle permissions / intents */
    lateinit var permissionHandler: PermissionResultManager

    /** The PanelAdapter class instance to manage the panels */
    lateinit var panelAdapter: PanelAdapter

    /** Track the active tab in the top header */
    enum class ActiveHeaderTab {
        NONE,
        PROFILE,
        NOTIFICATIONS,
        MENU,
    }

    private var _activeTab: ActiveHeaderTab = ActiveHeaderTab.NONE
    private var activeTab: ActiveHeaderTab
        get() = _activeTab
        set(value) {
            _activeTab = value
            updateActiveButton()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the launchers
        permissionHandler = PermissionResultManager()
    }

    override fun findViews() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        navProfileView = findViewById(R.id.nav_profile_view)
        navActionView = findViewById(R.id.nav_action_view)
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        profileIcon = findViewById(R.id.profile_img)
        accountLbl = findViewById(R.id.account_lbl)
        notificationIcon = findViewById(R.id.notifications_img)
        notificationsLbl = findViewById(R.id.notifications_lbl)
        activeNotificationIcon = findViewById(R.id.notifications_circle_img)
        menuIcon = findViewById(R.id.menu_img)
        moreLbl = findViewById(R.id.more_lbl)
    }

    override fun addClickListeners() {
        initialisePager()
        initialiseDrawerLogic()
        initializeNotifications()
        addRefreshSwipeUp()
    }

    /** Add listener for swipe up to refresh the notifications and set the style of the spinner */
    private fun addRefreshSwipeUp() {
        swipeRefreshLayout.setColorSchemeColors(getColor(R.color.colorAccent))
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE)

        swipeRefreshLayout.setOnRefreshListener {
            NotificationRepository().refreshNotifications(
                onResponse = {
                    // There is a check in NetworkManage whether there is new notification,
                    // so the header will be automatically updated, just set the isRefreshing property
                    // when the request finishes
                    swipeRefreshLayout.isRefreshing = false
                },
            )
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
            tab.setIcon(panelAdapter.getPanelIcon(position))
        }.attach()
    }

    /** Add click listeners for selecting item from the drawer and back button pressed */
    private fun initialiseDrawerLogic() {
        profileIcon.setOnClickListener {
            activeTab = ActiveHeaderTab.PROFILE
            drawerLayout.openDrawer(navProfileView)
        }

        menuIcon.setOnClickListener {
            activeTab = ActiveHeaderTab.MENU
            drawerLayout.openDrawer(navActionView)
        }

        // When user clicks outside the drawer or close it, update the active tab
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) { }

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                activeTab = ActiveHeaderTab.NONE
            }

            override fun onDrawerStateChanged(newState: Int) { }
        })

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

    /** Update the notifications icon when the activity is created and add click listener to display
     * user notifications
     */
    private fun initializeNotifications() {
        updateNotificationIcon()

        notificationIcon.setOnClickListener {
            activeTab = ActiveHeaderTab.NOTIFICATIONS
            Utils.getPanelAdapter().displayTemporaryPanel(NotificationsPanel())
        }
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
                panelAdapter.displayTemporaryPanel(ManageTemplatesPanel())
            }
            R.id.nav_manage_exercises -> {
                // Display the Muscle Groups and Exercises as temporary panel
                panelAdapter.displayTemporaryPanel(ManageExercisesPanel(BaseExercisePanel.Mode.SELECT_MUSCLE_GROUP))
            }
            R.id.nav_manage_teams -> {
                // Display the Manage Teams panel
                panelAdapter.displayTemporaryPanel(ManageTeamsPanel())
            }
            R.id.start_timer -> {
                // Show dialog to start timer
                SelectTimerDurationDialog(this).show()
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

                dialog.setConfirmButtonCallback {
                    UserRepository().logout(onSuccess = { Utils.onLogout() })
                }

                dialog.show()
            }
        }
    }

    /** Update the active button to indicate the selected one in the header */
    private fun updateActiveButton() {
        // Default all to unselected
        profileIcon.setBackgroundResource(R.drawable.icon_profile_inactive)
        notificationIcon.setBackgroundResource(R.drawable.icon_notification_inactive)
        menuIcon.setBackgroundResource(R.drawable.icon_menu_inactive)

        accountLbl.setTextColor(getColor(R.color.white))
        notificationsLbl.setTextColor(getColor(R.color.white))
        moreLbl.setTextColor(getColor(R.color.white))

        when (activeTab) {
            ActiveHeaderTab.PROFILE -> {
                profileIcon.setBackgroundResource(R.drawable.icon_profile_active)
                accountLbl.setTextColor(getColor(R.color.colorAccent))
            }
            ActiveHeaderTab.NOTIFICATIONS -> {
                notificationIcon.setBackgroundResource(R.drawable.icon_notification_active)
                notificationsLbl.setTextColor(getColor(R.color.colorAccent))
            }
            ActiveHeaderTab.MENU -> {
                menuIcon.setBackgroundResource(R.drawable.icon_menu_active)
                moreLbl.setTextColor(getColor(R.color.colorAccent))
            } else -> {
                // Noting to do
            }
        }
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
            profileImage.setBackgroundResource(R.drawable.icon_profile_default_picture)
        }

        // Set username to email or full name
        if (AppStateManager.user!!.fullName.isEmpty()) {
            username.text = AppStateManager.user!!.email
        } else {
            username.text = AppStateManager.user!!.fullName
        }
    }

    /** Set the red notification circle based on whether there is active notification */
    fun updateNotificationIcon() {
        if (AppStateManager.notification) {
            activeNotificationIcon.visibility = View.VISIBLE
        } else {
            activeNotificationIcon.visibility = View.GONE
        }

        // If the notifications panel is currently active, update the data
        Utils.getPanelAdapter().getNotificationsPanel()?.populatePanel()
    }

    /** Set the active tab (Notifications) to null */
    fun setNotificationButtonInactive() {
        activeTab = ActiveHeaderTab.NONE
    }
}