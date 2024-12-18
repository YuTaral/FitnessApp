package com.example.fitnessapp.utils

import com.example.fitnessapp.BaseActivity
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.models.UserModel
import com.example.fitnessapp.models.WorkoutModel
import java.lang.ref.WeakReference

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
                (Utils.getActivity() as MainActivity).setUserInDrawer()
            }
        }

    /** The active activity stored as a WeakReference to avoid potential memory leaks */
    private var _activeActivity: WeakReference<BaseActivity>? = null

    /** Getter and setter for the current active activity */
    var activeActivity: BaseActivity?
        get() = _activeActivity?.get()
        set(value) {
            _activeActivity = WeakReference(value)
        }

    /** Backing field for the selected workout */
    private var _workout: WorkoutModel? = null

    /** Getter for the selected workout */
    var workout: WorkoutModel?
        get() = _workout
        set(value) {
            _workout = value
            (activeActivity as MainActivity).updateActions()
        }
}