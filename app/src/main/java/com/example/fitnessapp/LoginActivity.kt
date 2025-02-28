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
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.managers.PermissionResultManager
import com.example.fitnessapp.managers.SharedPrefsManager
import com.example.fitnessapp.models.UserModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.utils.Utils
import com.google.android.material.textfield.TextInputLayout

/** Class to implement the logic for Login / Register */
class LoginActivity : BaseActivity() {
    override var layoutId = R.layout.activity_login

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

    private lateinit var permissionHandler: PermissionResultManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SharedPrefsManager.isFirstAppStart()) {
            permissionHandler = PermissionResultManager()
            askForPermissions()
        }


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
        email = findViewById<TextInputLayout>(R.id.email).editText!!
        password = findViewById<TextInputLayout>(R.id.password).editText!!
        emailReg = findViewById<TextInputLayout>(R.id.email_reg).editText!!
        passwordReg = findViewById<TextInputLayout>(R.id.password_reg).editText!!
        passwordConfirm = findViewById<TextInputLayout>(R.id.confirm_password).editText!!
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
        loginContainer.visibility = View.GONE

        email.setText("")
        password.setText("")

        registerContainer.visibility = View.VISIBLE
    }

    /** Display Login page */
    private fun displayLogin() {
        welcomeLbl.visibility = View.GONE
        registerContainer.visibility = View.GONE

        emailReg.setText("")
        passwordReg.setText("")
        passwordConfirm.setText("")

        loginContainer.visibility = View.VISIBLE
    }

    /** Register a new user */
    private fun register() {
        val email = emailReg.text.toString()
        val password = passwordReg.text.toString()
        val confirmPassword = passwordConfirm.text.toString()

        // Validate email
        if (!Utils.isValidEmail(email)) {
            Utils.validationFailed(emailReg, R.string.error_msg_invalid_email)
            return
        }

        // Validate passwords
        if (password.isEmpty()) {
            Utils.validationFailed(passwordReg, R.string.error_msg_blank_pass)
            return
        }
        if (password != confirmPassword) {
            Utils.validationFailed(passwordConfirm, R.string.error_msg_pass_match)
            return
        }

        // Client-side validation passed, send the register request
        UserRepository().register(email, password, onSuccess = {
            displayLogin()
        })
    }

    /** Login user */
    private fun login() {
        val emailValue = email.text.toString()
        val passwordValue = password.text.toString()

        // Validate email
        if (!Utils.isValidEmail(emailValue)) {
            Utils.validationFailed(email, R.string.error_msg_invalid_email)
            return
        }

        // Validate passwords
        if (passwordValue.isEmpty()) {
            Utils.validationFailed(password, R.string.error_msg_blank_pass)
            return
        }

        // Client-side validation passed, send the login request
        UserRepository().login(emailValue, passwordValue, onSuccess = { response ->
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

    /** Ask for permission when it's the first time starting the application */
    private fun askForPermissions() {
        // Ask the user to review the permissions now
        val dialog = AskQuestionDialog(this, AskQuestionDialog.Question.GRANT_PERMISSIONS)

        dialog.setConfirmButtonCallback {
            dialog.dismiss()

            // If that's the first start of the application on this device
            // Start asking for permissions one by one
            permissionHandler.cameraPermLauncher.launch(android.Manifest.permission.CAMERA)
        }

        dialog.show()

        // Set the variable to indicate that the app was already started and do not ask
        // for permissions again
        SharedPrefsManager.setFirstStartApp()
    }
}


