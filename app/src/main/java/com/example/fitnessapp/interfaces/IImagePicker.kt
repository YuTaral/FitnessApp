package com.example.fitnessapp.interfaces

import android.graphics.Bitmap

/** IImagePicker interface to define behavior for dialogs/panels
 * where image can be uploaded via album/camera */
interface IImagePicker {
    /** Callback to execute when image upload is successful */
    fun onImageUploadSuccess(bitmap: Bitmap)
}