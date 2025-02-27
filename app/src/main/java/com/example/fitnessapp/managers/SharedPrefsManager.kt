package com.example.fitnessapp.managers

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.fitnessapp.models.UserModel
import com.example.fitnessapp.utils.Utils

/** Shared prefs manager class to handle write / read data to / from shared preferences */
object SharedPrefsManager {
    private const val SECURE_PREFS_FILE_NAME = "secure_prefs"
    private const val AUTH_TOKEN_KEY = "auth_token"
    private const val SERIALIZED_USER_KEY = "serialized_user"
    private const val FIRST_START_KEY = "first_start"

    /** Return authorization token stored in the shared prefs, if it does not exist return empty string */
    fun getStoredToken(): String {
        return getSharedPref().getString(AUTH_TOKEN_KEY, null) ?: return ""
    }

    /** Return User model stored in the shared prefs, if it does not exist return null */
    fun getStoredUser(): UserModel? {
        val sharedPref = getSharedPref()
        val serializedUser = sharedPref.getString(SERIALIZED_USER_KEY, "") ?: ""

        if (serializedUser.isEmpty())
        {
            return null
        }

        return UserModel(serializedUser)
    }

    /** Save / Remove the authorization token in shared prefs
     * @param token the token if we need to save it, empty string if we need to remove it
     */
    fun updateTokenInPrefs(token: String) {
        val sharedPref = getSharedPref()

        if (token.isEmpty()) {
            sharedPref.edit().remove(AUTH_TOKEN_KEY).apply()
        } else {
            sharedPref.edit().putString(AUTH_TOKEN_KEY, token).apply()
        }
    }

    /** Save / Remove serialized user model in shared prefs
     * @param model the user model if we must save the user, null if we need to remove it
     */
    fun updateUserInPrefs(model: UserModel?) {
        val sharedPref = getSharedPref()

        if (model == null) {
            sharedPref.edit().remove(SERIALIZED_USER_KEY).apply()
        } else {
            sharedPref.edit().putString(SERIALIZED_USER_KEY, Utils.serializeObject(model)).apply()
        }
    }

    /** Return true if that's the first time the user is starting the app on the device, false otherwise */
    fun isFirstAppStart(): Boolean {
        val firstTime = getSharedPref().getString(FIRST_START_KEY, null) ?: ""

        return firstTime.isEmpty()
    }

    /** Set the variable to indicate that the app was already started once on the device */
    fun setFirstStartApp() {
        getSharedPref().edit().putString(FIRST_START_KEY, "N").apply()
    }

    /**
     * Create and return EncryptedSharedPreferences, retrying
     * creation if something goes wrong
     */
    private fun getSharedPref(): SharedPreferences {
        val context = Utils.getActivity()

        return try {
            createSharedPrefs()

        } catch (e: Exception) {
            // If error occurs, retry, the error may be due to "Signature/MAC verification failed",
            // which occurs when MasterKey or EncryptedSharedPreferences not working correctly
            // on this specific device or something goes wrong with

            // Delete the corrupted file and try to recreate shared prefs
            context.deleteSharedPreferences(SECURE_PREFS_FILE_NAME)
            createSharedPrefs()
        }
    }

    /** Create and return EncryptedSharedPreferences */
    private fun createSharedPrefs(): SharedPreferences
    {
        val masterKey = MasterKey.Builder(Utils.getActivity())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            Utils.getActivity(),
            SECURE_PREFS_FILE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}