package com.example.fitnessapp.dialogs

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fitnessapp.R
import com.example.fitnessapp.network.repositories.UserProfileRepository
import com.example.fitnessapp.utils.AppStateManager
import com.example.fitnessapp.utils.Constants
import com.example.fitnessapp.utils.Utils

/** Dialog to handle the logic for profile edit */
class EditProfileDialog(ctx: Context): BaseDialog(ctx) {
    override var layoutId = R.layout.edit_profile_dialog
    override var dialogTitleId = R.string.edit_profile_lbl

    private lateinit var profileImage: ImageView
    private lateinit var fullName: EditText
    private lateinit var removePictureBtn: Button
    private lateinit var saveBtn: Button

    override fun findViews() {
        super.findViews()

        profileImage = dialog.findViewById(R.id.profile_image)
        fullName = dialog.findViewById(R.id.full_name_txt)
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
            showImagePicker()
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
        profileImage.setBackgroundResource(R.drawable.icon_profile)
    }

    /** Show dialog to select from where to upload the image */
    private fun showImagePicker() {
        val dialog = AskQuestionDialog(Utils.getActivity(), AskQuestionDialog.Question.PROFILE_IMAGE_SELECTION)

        dialog.setLeftButtonCallback {
            openCamera()
            dialog.dismiss()
        }

        dialog.setRightButtonCallback {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Use Photo Picker for Android 13+
                openPhotoPicker()
            } else {
                // Handle older devices with permissions
                openGallery()
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    /** Open the camera to allow image capture */
    private fun openCamera() {
        if (Utils.getActivityResultHandler().checkPermissionGranted(Constants.Permissions.CAMERA)) {
            // Permission granted, open the camera
            Utils.getActivityResultHandler().cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else {
            // Ask for the permission
            Utils.getActivityResultHandler().cameraPermLauncher.launch(Constants.Permissions.CAMERA)
        }
    }

    /** Open the gallery to allow image selection */
    private fun openGallery() {
        var permissionGranted = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (Utils.getActivityResultHandler().checkPermissionGranted(Constants.Permissions.READ_MEDIA_IMAGES)) {
                permissionGranted = true
            } else {
                // Ask for the permission
                Utils.getActivityResultHandler().readMediaImagesPermLauncher.launch(Constants.Permissions.READ_MEDIA_IMAGES)
            }

        } else { // Below Android 13
            if (Utils.getActivityResultHandler().checkPermissionGranted(Constants.Permissions.READ_EXTERNAL_STORAGE)) {
                permissionGranted = true
            } else {
                // Ask for the permission
                Utils.getActivityResultHandler().readExternalStoragePermLauncher.launch(Constants.Permissions.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionGranted) {
            // Permission granted, open the gallery
            Utils.getActivityResultHandler().galleryLauncher.launch(Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }
    }

    /** Open the photo picker */
    private fun openPhotoPicker() {
        Utils.getActivityResultHandler().photoPickerLauncher
            .launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    /** Execute the callback when image capture is successful */
    fun onImageCaptureSuccess(bitmap: Bitmap) {
        val scaledBitmap = Utils.scaleBitmap(bitmap)

        if (scaledBitmap == null) {
            Utils.showToast(R.string.error_msg_failed_to_upload_image)
            return
        }

        // Set the scaled bitmap to the ImageView
        profileImage.setImageBitmap(scaledBitmap)
    }

    /** Execute the callback when image pick from gallery is successful
     * @param uri the selected image uri
     */
    fun onImagePickSuccess(uri: Uri) {
        val bitmap = Utils.scaleBitmap(uri)

        // Set the scaled bitmap to the ImageView
        if (bitmap == null) {
            Utils.showToast(R.string.error_msg_failed_to_upload_image)
            return
        }

        // Set the scaled bitmap to the ImageView
        profileImage.setImageBitmap(bitmap)
    }
}