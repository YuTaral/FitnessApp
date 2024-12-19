package com.example.fitnessapp.interfaces

import android.graphics.Bitmap

/** IImagePickerDialog interface to define behavior for dialogs/panels
 * where image can be uploaded via album/camera */
interface IImagePickerDialog {
    /** Callback to execute when image upload is successful */
    fun onImageUploadSuccess(bitmap: Bitmap)
}