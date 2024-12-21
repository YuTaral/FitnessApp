package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.models.UserModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.SharedPrefsManager
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for Login / Register */
class LoginActivity : BaseActivity() {
    override var layoutId = R.layout.login_activity

    private lateinit var loginContainer: ConstraintLayout
    private lateinit var registerContainer: ConstraintLayout
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var emailReg: EditText
    private lateinit var passwordReg: EditText
    private lateinit var passwordConfirm: EditText
    private lateinit var clickForRegisterLbl: TextView
    private lateinit var clickForLoginLbl: TextView
    private lateinit var registerBtn: Button
    private lateinit var loginBtn: Button
    private lateinit var welcomeLbl: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Try to login automatically
        val token = SharedPrefsManager.getStoredToken()
        val userModel = SharedPrefsManager.getStoredUser()

        if (token.isNotEmpty() && userModel != null) {
            // Validate the token
            UserRepository().validateToken(token, onSuccess = {
                // Set the logged in user and start the main activity
                AppStateManager.user = userModel
                startMainActivity()
            }, onFailure = {
                displayLogin()
            })
        } else {
            displayLogin()
        }
    }

    /** Find the views in the activity */
    override fun findViews() {
        loginContainer = findViewById(R.id.login_container)
        registerContainer = findViewById(R.id.register_container)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        emailReg = findViewById(R.id.email_reg)
        passwordReg = findViewById(R.id.password_reg)
        passwordConfirm = findViewById(R.id.confirm_password)
        clickForRegisterLbl = findViewById(R.id.click_for_register_lbl)
        clickForLoginLbl = findViewById(R.id.click_for_login_lbl)
        registerBtn = findViewById(R.id.register_btn)
        loginBtn = findViewById(R.id.login_btn)
        welcomeLbl = findViewById(R.id.welcome_lbl)
    }

    /** Add click listeners to the views in the activity */
    override fun addClickListeners() {
        clickForRegisterLbl.setOnClickListener { displayRegister() }
        clickForLoginLbl.setOnClickListener { displayLogin() }
        registerBtn.setOnClickListener { register() }
        loginBtn.setOnClickListener{ login() }

        password.setOnEditorActionListener { v, actionId, event ->
            // Check if the action is the "Done" action
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER
                        && event.action == KeyEvent.ACTION_DOWN)) {

                Utils.closeKeyboard(v)
                login()
                true

            } else {
                false
            }
        }

        passwordConfirm.setOnEditorActionListener { v, actionId, event ->
            // Check if the action is the "Done" action
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER
                        && event.action == KeyEvent.ACTION_DOWN)) {

                Utils.closeKeyboard(v)
                register()
                true

            } else {
                false
            }
        }
    }

    /** Display Register page */
    private fun displayRegister() {
        welcomeLbl.visibility = View.GONE
        Utils.hideViewWithFade(loginContainer)

        email.setText("")
        password.setText("")

        Utils.displayViewWithFade(registerContainer)
    }

    /** Display Login page */
    private fun displayLogin() {
        welcomeLbl.visibility = View.GONE
        Utils.hideViewWithFade(registerContainer)

        emailReg.setText("")
        passwordReg.setText("")
        passwordConfirm.setText("")

        Utils.displayViewWithFade(loginContainer)
    }

    /** Register a new user */
    private fun register() {
        val email = emailReg.text.toString()
        val password = passwordReg.text.toString()
        val confirmPassword = passwordConfirm.text.toString()

        // Validate email
        if (!Utils.isValidEmail(email)) {
            Utils.showToast(R.string.error_msg_invalid_email)
            return
        }

        // Validate passwords
        if (password.isEmpty()) {
            Utils.showToast( R.string.error_msg_blank_pass)
            return
        }
        if (password != confirmPassword) {
            Utils.showToast(R.string.error_msg_pass_match)
            return
        }

        // Client-side validation passed, send the register request
        UserRepository().register(email, password, onSuccess = {
            displayLogin()
        })
    }

    /** Login user */
    private fun login() {
        val email = email.text.toString()
        val password = password.text.toString()

        // Validate email
        if (!Utils.isValidEmail(email)) {
            Utils.showToast(R.string.error_msg_invalid_email)
            return
        }

        // Validate passwords
        if (password.isEmpty()) {
            Utils.showToast( R.string.error_msg_blank_pass)
            return
        }

        // Client-side validation passed, send the register request
        UserRepository().login(email, password, onSuccess = { response ->
            // Set the logged in user and start the Main Activity
            AppStateManager.user = UserModel(response.data[0])

            // Update the service with the token
            APIService.updateToken(response.data[1])

            if (response.data.size > 2) {
                AppStateManager.workout = WorkoutModel(response.data[2])
            }

            startMainActivity()
        })
    }

    /** Start the app main activity */
    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}


