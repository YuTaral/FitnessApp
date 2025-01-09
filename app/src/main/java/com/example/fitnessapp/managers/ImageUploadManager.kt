package com.example.fitnessapp.managers

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fitnessapp.dialogs.AskQuestionDialog
import com.example.fitnessapp.utils.Utils
import java.io.InputStream

/** Image upload class to handle image uploading from the album or camera */
object ImageUploadManager {
    private const val IMAGE_WIDTH = 256
    private const val IMAGE_HEIGHT = 256

    /** Show dialog to select from where to upload the image */
    fun showImagePicker() {
        val dialog = AskQuestionDialog(Utils.getActivity(), AskQuestionDialog.Question.IMAGE_SELECTION_OPTIONS)

        dialog.setConfirmButtonCallback {
            openCamera()
            dialog.dismiss()
        }

        dialog.setCancelButtonCallback {
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
        if (Utils.getActivityResultHandler().checkPermissionGranted(android.Manifest.permission.CAMERA)) {
            // Permission granted, open the camera
            Utils.getActivityResultHandler().cameraLauncher.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
        } else {
            // Ask for the permission
            Utils.getActivityResultHandler().cameraPermLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    /** Open the gallery to allow image selection */
    private fun openGallery() {
        var permissionGranted = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (Utils.getActivityResultHandler().checkPermissionGranted(android.Manifest.permission.READ_MEDIA_IMAGES)) {
                permissionGranted = true
            } else {
                // Ask for the permission
                Utils.getActivityResultHandler().readMediaImagesPermLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            }

        } else { // Below Android 13
            if (Utils.getActivityResultHandler().checkPermissionGranted(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionGranted = true
            } else {
                // Ask for the permission
                Utils.getActivityResultHandler().readExternalStoragePermLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionGranted) {
            // Permission granted, open the gallery
            Utils.getActivityResultHandler().galleryLauncher.launch(
                Intent(
                    Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            )
        }
    }

    /** Open the photo picker */
    private fun openPhotoPicker() {
        Utils.getActivityResultHandler().photoPickerLauncher
            .launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    /**
     * Scales a bitmap to fit within the specified width and height while maintaining aspect ratio.
     * Return the bitmap is success, null otherwise
     * @param bitmap the image bitmap
     */
    fun scaleBitmap(bitmap: Bitmap): Bitmap? {
        try {
            val width = bitmap.width
            val height = bitmap.height

            // Calculate the scaling factor while maintaining the aspect ratio
            val scaleFactor = minOf(IMAGE_WIDTH.toFloat() / width, IMAGE_HEIGHT.toFloat() / height)

            return if (scaleFactor < 1) {
                val matrix = Matrix()
                matrix.postScale(scaleFactor, scaleFactor)

                // Create a new scaled bitmap
                Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
            } else {
                // If the bitmap is already small, return it as is
                bitmap
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Create and scales a bitmap from the provided uri to fit within the specified width and height
     * while maintaining aspect ratio. Return the bitmap is success, null otherwise
     * @param uri the image uri
     */
    fun scaleBitmap(uri: Uri): Bitmap? {
        try {
            val contentResolver = Utils.getActivity().contentResolver

            val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // For API 28+ (Pie and above), use ImageDecoder with scaling
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                    decoder.setTargetSampleSize(4)
                }

            } else {
                // For older APIs, scale down using BitmapFactory
                val inputStream: InputStream? = contentResolver.openInputStream(uri)

                // Decode only the image dimensions first
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, options)
                inputStream?.close()

                // Calculate the appropriate sample size
                options.inSampleSize =
                    calculateInSampleSize(options.outWidth, options.outHeight)
                options.inJustDecodeBounds = false

                // Decode the scaled-down bitmap
                val scaledInputStream: InputStream? = contentResolver.openInputStream(uri)
                val scaledBitmap = BitmapFactory.decodeStream(scaledInputStream, null, options)
                scaledInputStream?.close()
                scaledBitmap!!
            }

            return bitmap

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /** Calculate a sample size to scale down the image.
     * @param rawWidth the actual image width
     * @param rawHeight the actual image height
     */
    private fun calculateInSampleSize(rawWidth: Int, rawHeight: Int): Int {
        var inSampleSize = 1

        if (rawHeight > IMAGE_HEIGHT || rawWidth > IMAGE_WIDTH) {
            val halfHeight = rawHeight / 2
            val halfWidth = rawWidth / 2

            while ((halfHeight / inSampleSize) >= IMAGE_HEIGHT && (halfWidth / inSampleSize) >= IMAGE_WIDTH) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}