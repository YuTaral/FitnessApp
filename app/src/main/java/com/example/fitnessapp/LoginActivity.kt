package com.example.fitnessapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.fitnessapp.models.UserModel
import com.example.fitnessapp.models.WorkoutModel
import com.example.fitnessapp.network.APIService
import com.example.fitnessapp.network.NetworkManager
import com.example.fitnessapp.utils.StateEngine
import com.example.fitnessapp.utils.Utils

/** Class to hold the logic for Login / Register */
class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        StateEngine.activeActivity = this

        findViewById<TextView>(R.id.click_for_register_lbl).setOnClickListener { displayRegister() }
        findViewById<TextView>(R.id.click_for_login_lbl).setOnClickListener { displayLogin() }
        findViewById<Button>(R.id.register_btn).setOnClickListener { register() }
        findViewById<Button>(R.id.login_btn).setOnClickListener{ login() }

//        autoLogin()
    }

    /** Display Register page */
    private fun displayRegister() {
        findViewById<ConstraintLayout>(R.id.login_container).visibility = View.GONE
        findViewById<TextView>(R.id.email).text = ""
        findViewById<TextView>(R.id.password).text = ""

        findViewById<ConstraintLayout>(R.id.register_container).visibility = View.VISIBLE
    }

    /** Display Login page */
    private fun displayLogin() {
        findViewById<ConstraintLayout>(R.id.register_container).visibility = View.GONE
        findViewById<TextView>(R.id.email_reg).text = ""
        findViewById<TextView>(R.id.password_reg).text = ""
        findViewById<TextView>(R.id.confirm_password).text = ""

        findViewById<ConstraintLayout>(R.id.login_container).visibility = View.VISIBLE
    }

    /** Register a new user */
    private fun register() {
        val email = findViewById<TextView>(R.id.email_reg).text.toString()
        val password = findViewById<TextView>(R.id.password_reg).text.toString()
        val confirmPassword = findViewById<TextView>(R.id.confirm_password).text.toString()

        // Validate email
        if (!Utils.isValidEmail(email)) {
            Utils.showMessage(R.string.error_msg_invalid_email)
            return
        }

        // Validate passwords
        if (password.isEmpty()) {
            Utils.showMessage( R.string.error_msg_blank_pass)
            return
        }
        if (password != confirmPassword) {
            Utils.showMessage(R.string.error_msg_pass_match)
            return
        }

        // Client-side validation passed, send the register request
        NetworkManager.sendRequest(
            APIService.instance.register(mapOf("email" to email, "password" to password)),
            onSuccessCallback = {
                Utils.showMessage(R.string.user_registered)
                displayLogin()
            })
    }

    /** Login user */
    private fun login() {
        val email = findViewById<TextView>(R.id.email).text.toString()
        val password = findViewById<TextView>(R.id.password).text.toString()

        // Validate email
        if (!Utils.isValidEmail(email)) {
            Utils.showMessage(R.string.error_msg_invalid_email)
            return
        }

        // Validate passwords
        if (password.isEmpty()) {
            Utils.showMessage( R.string.error_msg_blank_pass)
            return
        }

        // Client-side validation passed, send the register request
        NetworkManager.sendRequest(
            APIService.instance.login(mapOf("email" to email, "password" to password)),
            onSuccessCallback = { response ->
                // Set the logged in user and start the Main Activity
                StateEngine.user = UserModel(response.returnData[0])

                if (response.returnData.size > 1) {
                    StateEngine.workout = WorkoutModel(response.returnData[1])
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        )
    }

//    fun autoLogin() {
//        findViewById<TextView>(R.id.email).text = "test@abv.bg"
//        findViewById<TextView>(R.id.password).text = "123"
//        login()
//    }
}


