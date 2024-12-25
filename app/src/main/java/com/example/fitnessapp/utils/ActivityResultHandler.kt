package com.example.fitnessapp.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.example.fitnessapp.BaseActivity
import com.example.fitnessapp.R
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.interfaces.IImagePicker

/** Class to handle the logic when requesting permissions / launching specific result launcher */
class ActivityResultHandler {
    private var activity: BaseActivity = Utils.getActivity()

    var cameraPermLauncher: ActivityResultLauncher<String>
    var readMediaImagesPermLauncher: ActivityResultLauncher<String>
    var readExternalStoragePermLauncher: ActivityResultLauncher<String>
    lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    lateinit var photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    init {
        val readMediaPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        // Initialize the permission launchers
        cameraPermLauncher = initializeRequestPermissionLaunchers(android.Manifest.permission.CAMERA)
        readMediaImagesPermLauncher = initializeRequestPermissionLaunchers(readMediaPermission)
        readExternalStoragePermLauncher = initializeRequestPermissionLaunchers(android.Manifest.permission.READ_EXTERNAL_STORAGE)

        // Initialize the activity result launchers
        initializeActivityResultLaunchers()
    }

    /** Initialize the launchers for requesting permissions */
    private fun initializeRequestPermissionLaunchers(permission: String): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                when (permission) {
                    // Execute the action based on the current requested permission
                    (android.Manifest.permission.CAMERA) -> {
                        cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
                    }

                    (android.Manifest.permission.READ_MEDIA_IMAGES),
                    (android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                        galleryLauncher.launch(Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
                    }

                }
            } else {
                // Handle permission denial
                // When the camera permission is set to "Do not ask again" or being set to that option,
                // the BaseActivity onPause is triggered, but onRestart/onResume are not, so manually set the activity
                // otherwise it will remain to null
                AppStateManager.activeActivity = activity

                if (!shouldShowRequestPermissionRationale(activity, permission)) {
                    // Permission set to "Don't ask again" or permanently denied, open the settings
                    showSettingsDialog()
                } else {
                    // Permission denied
                    Utils.showToast(R.string.permission_denied_message)
                }
            }
        }
    }

    /** Initialize the launchers for open camera / photos */
    private fun initializeActivityResultLaunchers() {
        // Initialize camera launcher
        cameraLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.extras != null) {

                val capturedImageBitmap: Bitmap? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // API 33+ (Android 13 and above)
                    result.data?.extras?.getParcelable("data", Bitmap::class.java)
                } else {
                    // For older versions below API 33
                    @Suppress("DEPRECATION")
                    result.data?.extras?.getParcelable("data")
                }

                if (capturedImageBitmap == null) {
                    Utils.showToast(R.string.error_msg_failed_to_capture_image)
                    return@registerForActivityResult
                }

                onLauncherResultOk(capturedImageBitmap)

            } else {
                Utils.showToast(R.string.error_msg_failed_to_capture_image)
            }
        }

        // Initialize gallery launcher
        galleryLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
                onLauncherResultOk(result.data?.data!!)

            } else {
                Utils.showToast(R.string.error_msg_no_media)
            }
        }

        // Initialize photo picker launcher
        photoPickerLauncher = activity.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // The PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            // does not trigger BaseActivity.onResume(), but triggers
            // BaseActivity.onPause(), probably because the activity is not fully closed and the
            // gallery is partially opened, so the currently activeActivity remains null.
            // Manually update it here
            AppStateManager.activeActivity = activity

            if (uri != null) {
                onLauncherResultOk(uri)
            } else {
                Utils.showToast(R.string.error_msg_no_media)
            }
        }
    }

    /** Execute the callback when launcher result is OK
     * @param bitmap the bitmap after image successful image capture
     */
    private fun onLauncherResultOk(bitmap: Bitmap) {
        val scaledBitmap = ImageUploadManager.scaleBitmap(bitmap)

        if (scaledBitmap == null) {
            Utils.showToast(R.string.error_msg_failed_to_upload_image)
            return
        }

        executeImageUploadSuccess(bitmap)
    }

    /** Execute the callback when launcher result is OK
     * @param uri the image uri
     */
    private fun onLauncherResultOk(uri: Uri) {
        val bitmap = ImageUploadManager.scaleBitmap(uri)

        if (bitmap == null) {
            Utils.showToast(R.string.error_msg_failed_to_upload_image)
            return
        }

        executeImageUploadSuccess(bitmap)
    }

    /** Execute the callback of the active IImageSelector
     * @param bitmap the bitmap after image successful image capture
     */
    private fun executeImageUploadSuccess(bitmap: Bitmap) {
        val imageSelectorDialog = getActiveIImageSelector()

        if (imageSelectorDialog == null) {
            val imageSelectorPanel = Utils.getPanelAdapter().getTeamPanel()

            if (imageSelectorPanel == null) {
                Utils.showToast(R.string.error_msg_unexpected)
                return
            }

            // Image selector is panel
            imageSelectorPanel.onImageUploadSuccess(bitmap)
            return
        }

        // Image selector is dialog
        imageSelectorDialog.onImageUploadSuccess(bitmap)
    }

    /** Ask the user to open settings and change the permission */
    private fun showSettingsDialog() {
        val dialog = AskQuestionDialog(activity, AskQuestionDialog.Question.ALLOW_CAMERA_PERMISSION)

        dialog.setLeftButtonCallback {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", activity.packageName, null))

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.startActivity(intent)

            dialog.dismiss()
        }

        dialog.show()
    }

    /** Return the active IImageSelector stored in the active dialogs property of the main activity
     *  If no such dialog  exists, null is returned
     */
    private fun getActiveIImageSelector(): IImagePicker? {
        val dialogs = activity.activeDialogs.filterIsInstance<IImagePicker>()

        if (dialogs.isEmpty()) {
            return null
        }

        return dialogs[0]
    }

    /** Return true the permission is granted, false otherwise
     * @param permission the permission
     */
    fun checkPermissionGranted(permission: String): Boolean {
       return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }
}