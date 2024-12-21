package com.example.fitnessapp.utils

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.fitnessapp.models.UserModel

/** Shared prefs manager class to handle write / read data to / from shared preferences */
object SharedPrefsManager {
    private const val SECURE_PREFS_FILE_NAME = "secure_prefs"
    private const val AUTH_TOKEN_KEY = "auth_token"
    private const val SERIALIZED_USER_KEY = "serialized_user"

    /** Return authorization token stored in the shared prefs, if it does not exist return empty string */
    fun getStoredToken(): String {
        return getSharedPref().getString(AUTH_TOKEN_KEY, null) ?: return ""
    }

    /** Return User model stored in the shared prefs, if it does not exist return null */
    fun getStoredUser(): UserModel? {
        val sharedPref = getSharedPref()
        val serializedUser = sharedPref.getString(SERIALIZED_USER_KEY, null) ?: ""

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

    /** Create and return SharedPreferences object using encryption */
    private fun getSharedPref(): SharedPreferences {
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