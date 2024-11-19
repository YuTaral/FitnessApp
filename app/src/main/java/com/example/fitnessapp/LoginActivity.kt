package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.models.UserModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for Login / Register */
class LoginActivity : AppCompatActivity() {
    private lateinit var loginContainer: ConstraintLayout
    private lateinit var registerContainer: ConstraintLayout
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var emailReg: EditText
    private lateinit var passwordReg: EditText
    private lateinit var passwordConfirm: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        StateEngine.activeActivity = this

        // Find the views
        loginContainer = findViewById(R.id.login_container)
        registerContainer = findViewById(R.id.register_container)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        emailReg = findViewById(R.id.email_reg)
        passwordReg = findViewById(R.id.password_reg)
        passwordConfirm = findViewById(R.id.confirm_password)

        // Add click listeners
        findViewById<TextView>(R.id.click_for_register_lbl).setOnClickListener { displayRegister() }
        findViewById<TextView>(R.id.click_for_login_lbl).setOnClickListener { displayLogin() }
        findViewById<Button>(R.id.register_btn).setOnClickListener { register() }
        findViewById<Button>(R.id.login_btn).setOnClickListener{ login() }

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
        Utils.hideViewWithFade(loginContainer)

        email.setText("")
        password.setText("")

        Utils.displayViewWithFade(registerContainer)
    }

    /** Display Login page */
    private fun displayLogin() {
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
            StateEngine.user = UserModel(response.responseData[0])

            if (response.responseData.size > 2) {
                StateEngine.workout = WorkoutModel(response.responseData[2])
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        })
    }
}


