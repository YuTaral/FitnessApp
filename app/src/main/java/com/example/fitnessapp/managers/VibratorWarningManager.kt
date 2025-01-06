package com.example.fitnessapp.managers

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibratorManager

/** Class handle vibrations logic to warn the users when needed */
object VibratorWarningManager {

    /** Make vibration with the specified patter */
    fun makeVibration(context: Context, pattern: LongArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibrator = (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                    as VibratorManager).defaultVibrator

            if (vibrator.hasVibrator()) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            }
        }
    }
}