package com.example.fitnessapp.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Button
import android.widget.EditText
import com.example.fitnessapp.R
import com.example.fitnessapp.network.repositories.UserRepository
import com.example.fitnessapp.utils.Utils

/** Change password dialog to implement change password functionality
 * @param ctx the context
 */
@SuppressLint("InflateParams")
class ChangePasswordDialog(ctx: Context): BaseDialog(ctx) {
    override var layoutId = R.layout.change_password_dialog
    override var dialogTitleId = R.string.change_password_lbl

    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var saveBtn: Button

    override fun findViews() {
        super.findViews()

        oldPassword = dialogView.findViewById(R.id.old_pass_txt)
        newPassword = dialogView.findViewById(R.id.new_pass_txt)
        confirmPassword = dialogView.findViewById(R.id.confirm_new_pass_txt)
        saveBtn = dialogView.findViewById(R.id.save_btn)
    }

    override fun populateDialog() {}

    override fun addClickListeners() {
        super.addClickListeners()

        saveBtn.setOnClickListener{ save() }
    }

    /** Executed on Save button click.
     * Perform a validation and sends a request to change the password
     */
    private fun save() {
        // Validate the password
        if (!validate()) {
            return
        }

        // Send request to change the password
        UserRepository().changePassword(oldPassword.text.toString(), newPassword.text.toString(),
            onSuccess = { dismiss() }
        )
    }

    /** Validate all password fields are not empty and the new password matches with the confirm
     * password field
     */
    private fun validate(): Boolean {
        if (oldPassword.text.isEmpty()) {
            Utils.validationFailed(oldPassword, R.string.error_msg_blank_pass)
            return false
        }

        if (newPassword.text.isEmpty()) {
            Utils.validationFailed(newPassword, R.string.error_msg_blank_pass)
            return false
        }

        if (confirmPassword.text.isEmpty()) {
            Utils.validationFailed(confirmPassword, R.string.error_msg_blank_pass)
            return false
        }

        if (confirmPassword.text.toString() != newPassword.text.toString()) {
            Utils.validationFailed(confirmPassword, R.string.error_msg_pass_match)
            return false
        }

        if (oldPassword.text.toString() == newPassword.text.toString()) {
            Utils.validationFailed(newPassword, R.string.error_msg_pass_matches)
            return false
        }

        return true
    }
}