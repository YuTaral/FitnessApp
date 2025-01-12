package com.example.fitnessapp.dialogs

import android.content.Context
import android.graphics.Bitmap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.fitnessapp.R
import com.example.fitnessapp.interfaces.IImagePicker
import com.example.fitnessapp.managers.AppStateManager
import com.example.fitnessapp.managers.ImageUploadManager
import com.example.fitnessapp.network.repositories.UserProfileRepository
import com.example.fitnessapp.utils.Utils
import com.google.android.material.textfield.TextInputLayout

/** Dialog to handle the logic for profile edit */
class EditProfileDialog(ctx: Context): BaseDialog(ctx), IImagePicker {
    override var layoutId = R.layout.dialog_edit_profile
    override var dialogTitleId = R.string.edit_profile_lbl

    private lateinit var profileImage: ImageView
    private lateinit var fullName: EditText
    private lateinit var removePictureBtn: Button
    private lateinit var saveBtn: Button

    override fun findViews() {
        super.findViews()

        profileImage = dialog.findViewById(R.id.team_image)
        fullName = dialog.findViewById<TextInputLayout>(R.id.full_name_txt).editText!!
        removePictureBtn = dialog.findViewById(R.id.remove_pic_btn)
        saveBtn = dialog.findViewById(R.id.save_btn)
    }

    override fun populateDialog() {
        // Set full name if there is
        if (AppStateManager.user!!.fullName.isNotEmpty()) {
            fullName.setText(AppStateManager.user!!.fullName)
        }

        // Set the profile picture if there is
        if (AppStateManager.user!!.profileImage.isNotEmpty()) {
            try {
                profileImage.setImageBitmap(Utils.convertStringToBitmap(AppStateManager.user!!.profileImage))

            } catch (e: IllegalArgumentException) {
                // Leave the default image
                e.printStackTrace()
            }
        }
    }

    override fun addClickListeners() {
        super.addClickListeners()

        profileImage.setOnClickListener {
            ImageUploadManager.showImagePicker()
        }
        saveBtn.setOnClickListener {
            // Disable the button as this request takes more time and prevent the possibility of
            // clicking it twice
            saveBtn.isEnabled = false
            save()
        }
        removePictureBtn.setOnClickListener {
            removePicture()
        }
    }

    /** Execute on save button click to save the changes */
    private fun save() {
        val image = Utils.encodeImageToString(profileImage)
        val fullNameText = fullName.text.toString()

        val updatedUser = AppStateManager.user!!
        updatedUser.fullName = fullNameText
        updatedUser.profileImage = image

        UserProfileRepository().updateUserProfile(updatedUser, onSuccess = { newUser ->
            dismiss()
            AppStateManager.user = newUser },
            onFailure = { saveBtn.isEnabled = true }
        )
    }

    /** Remove picture button handler to clear the profile picture and return to the default one */
    private fun removePicture() {
        profileImage.setImageBitmap(null)
        profileImage.setBackgroundResource(R.drawable.icon_profile_default_picture)
    }

    /** Execute the callback when image capture is successful */
    override fun onImageUploadSuccess(bitmap: Bitmap) {
        profileImage.setImageBitmap(bitmap)
    }
}