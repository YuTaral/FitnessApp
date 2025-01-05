package com.example.fitnessapp.dialogs

import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Button
import android.widget.TextView
import com.example.fitnessapp.R
import com.example.fitnessapp.interfaces.INeedResumeDialog
import com.example.fitnessapp.managers.CustomNotificationManager
import com.example.fitnessapp.utils.Utils

/** Timer dialog - start a timer for the specified amount of time */
class TimerDialog(ctx: Context, s: Int, onFinish: () -> Unit): BaseDialog(ctx), INeedResumeDialog {
    override var layoutId = R.layout.dialog_timer
    override var dialogTitleId = R.string.timer_lbl

    private var context = ctx
    private var seconds = s
    private var secondsLeft = seconds
    private var onFinishCallback = onFinish

    private lateinit var timeLeft: TextView
    private lateinit var doneBtn: Button
    private lateinit var startPauseBtn: Button

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var vibrator: Vibrator

    override fun findViews() {
        super.findViews()

        timeLeft = dialog.findViewById(R.id.time_left)
        doneBtn = dialog.findViewById(R.id.done_btn)
        startPauseBtn = dialog.findViewById(R.id.start_pause_btn)
    }

    override fun populateDialog() {
        timeLeft.text = seconds.toString()
    }

    override fun addClickListeners() {
        super.addClickListeners()

        startPauseBtn.setOnClickListener { startStopTimer() }

        doneBtn.setOnClickListener {
            onFinishCallback()
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
        countDownTimer.cancel()
        vibrator.cancel()
    }

    /** Resume the dialog when the app was minimized - check if the timer finished */
    override fun resume() {
        if (::timeLeft.isInitialized && timeLeft.text == "0") {
            onTimerFinish()
        }
    }

    /** Execute the logic when the timer finishes */
    private fun onTimerFinish() {
        try {
            if (Utils.isAppMinimized()) {
                // Send a notification if the app is in the background
                CustomNotificationManager(context, R.string.time_is_up_lbl, R.string.time_finished_lbl).sendNotification()
            }

            startPauseBtn.text = context.getText(R.string.restart_btn)

            timeLeft.text = context.getText(R.string.time_is_up_lbl)
            timeLeft.textSize = 40f

            // Vibrate the device
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                vibrator = (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                        as VibratorManager).defaultVibrator

                if (vibrator.hasVibrator()) {
                    val pattern = longArrayOf(0, 1000, 500, 1000, 500, 1000)
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Execute start / pause timer */
    private fun startStopTimer() {
        when (startPauseBtn.text) {
            (context.getText(R.string.start_btn)) -> {
                // Resume the timer with the seconds left
                resumeTimer(secondsLeft)
                startPauseBtn.text = context.getText(R.string.pause_btn)
            }

            (context.getText(R.string.pause_btn)) -> {
                // Pause the timer
                countDownTimer.cancel()
                startPauseBtn.text = context.getText(R.string.start_btn)
            }

            else -> {
                // Restart the timer
                resumeTimer(seconds + 1)
                startPauseBtn.text = context.getText(R.string.pause_btn)
                vibrator.cancel()
            }
        }
    }

    /** Resume the timer
     * @param seconds the seconds to start from
     */
    private fun resumeTimer(seconds: Int) {
        countDownTimer = object: CountDownTimer(seconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft.text = (millisUntilFinished / 1000).toString()
                secondsLeft = timeLeft.text.toString().toInt()
            }

            override fun onFinish() {
                onTimerFinish()
            }
        }

        countDownTimer.start()
    }
}