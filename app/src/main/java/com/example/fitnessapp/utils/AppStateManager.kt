package com.example.fitnessapp.utils

import com.example.fitnessapp.BaseActivity
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.adapters.PanelAdapter
import com.example.fitnessapp.models.UserModel
import com.example.fitnessapp.models.WorkoutModel

/** Object to define the current state (hold current activity, logged in user, etc... */
object AppStateManager {
    /** Backing field for the logged in user */
    private var _user: UserModel? = null

    /** Getter and Setter for the logged-in user */
    var user: UserModel?
        get() = _user
        set(value) {
            _user = value
            Utils.updateUserInPrefs(_user)

            if (_user != null && activeActivity is MainActivity) {
                Utils.getMainActivity().setUserInDrawer()
            }
        }

    /** The active activity */
    lateinit var activeActivity: BaseActivity

    /** The panels pager adapter */
    lateinit var panelAdapter: PanelAdapter

    /** Backing field for the selected workout */
    private var _workout: WorkoutModel? = null

    /** Getter for the selected workout */
    var workout: WorkoutModel?
        get() = _workout
        set(value) {
            _workout = value
            (activeActivity as MainActivity).updateActions()
        }

    /** Used to track when change in the selected workout occurred and
     *  latest workouts refresh is needed
     */
    var refreshWorkouts = false

}