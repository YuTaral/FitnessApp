package com.example.fitnessapp.dialogs

import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import com.example.fitnessapp.BaseActivity
import com.example.fitnessapp.R
import com.example.fitnessapp.interfaces.INeedResumeDialog
import com.example.fitnessapp.managers.CustomNotificationManager
import com.example.fitnessapp.managers.VibratorWarningManager
import com.example.fitnessapp.utils.Utils
import com.mikhaellopez.circularprogressbar.CircularProgressBar

/** Timer dialog - start a timer for the specified amount of time */
class TimerDialog(ctx: BaseActivity, titleId: Int, time: Int, auto: Boolean): BaseDialog(ctx), INeedResumeDialog {
    override var layoutId = R.layout.dialog_timer
    override var dialogTitleId = titleId

    private var activity = ctx
    private var seconds = time
    private var secondsLeft = seconds
    private var autoStart = auto

    private lateinit var progressBar: CircularProgressBar
    private lateinit var timeLeft: TextView
    private lateinit var doneBtn: Button
    private lateinit var startPauseBtn: Button

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var onFinishCallback: () -> Unit

    override fun findViews() {
        super.findViews()

        progressBar = dialog.findViewById(R.id.progress_bar)
        timeLeft = dialog.findViewById(R.id.time_left)
        doneBtn = dialog.findViewById(R.id.done_btn)
        startPauseBtn = dialog.findViewById(R.id.start_pause_btn)
    }

    override fun populateDialog() {
        setRemainingTime(seconds)
        progressBar.setProgressWithAnimation(secondsLeft.toFloat(), 100)
        progressBar.progressMax = seconds.toFloat()
    }

    override fun addClickListeners() {
        super.addClickListeners()

        startPauseBtn.setOnClickListener { startStopTimer() }

        doneBtn.setOnClickListener {
            if (::onFinishCallback.isInitialized) {
                onFinishCallback()
            }
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()

        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }

    /** Resume the dialog when the app was minimized - check if the timer finished */
    override fun resume() {
        if (::timeLeft.isInitialized && timeLeft.text == "0") {
            onTimerFinish()
        }
    }

    override fun show() {
        super.show()

        val permission = Utils.getActivityResultHandler().getNotificationsPermString()

        // Ask for permission to send notifications
        if (!Utils.getActivityResultHandler().checkPermissionGranted(permission)) {
            Utils.getActivityResultHandler().notificationPermLauncher.launch(permission)
        } else if (autoStart) {
            startStopTimer()
        }
    }

    /** Execute the logic when the timer finishes */
    private fun onTimerFinish() {
        try {
            if (Utils.isAppMinimized()) {
                // Send a notification if the app is in the background
                CustomNotificationManager(activity, R.string.time_is_up_lbl, R.string.time_finished_lbl).sendNotification()
            }

            startPauseBtn.text = activity.getText(R.string.restart_btn)

            timeLeft.text = activity.getText(R.string.time_is_up_lbl)

            // Vibrate the device
            VibratorWarningManager.makeVibration(activity, longArrayOf(0, 500, 500, 500, 500, 500))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** Execute start / pause timer */
    private fun startStopTimer() {
        when (startPauseBtn.text) {
            (activity.getText(R.string.start_btn)) -> {
                // Resume the timer with the seconds left
                resumeTimer(secondsLeft)
                startPauseBtn.text = activity.getText(R.string.pause_btn)
            }

            (activity.getText(R.string.pause_btn)) -> {
                // Pause the timer
                countDownTimer.cancel()
                startPauseBtn.text = activity.getText(R.string.start_btn)
            }

            else -> {
                // Restart the timer
                resumeTimer(seconds)
                startPauseBtn.text = activity.getText(R.string.pause_btn)
            }
        }
    }

    /** Resume the timer
     * @param seconds the seconds to start from
     */
    private fun resumeTimer(seconds: Int) {
        countDownTimer = object: CountDownTimer(seconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = (millisUntilFinished / 1000).toInt()
                secondsLeft = totalSeconds

                // Set the text and update the progress bar
                setRemainingTime(totalSeconds)
                progressBar.setProgressWithAnimation(totalSeconds.toFloat(), 100)
            }

            override fun onFinish() {
                onTimerFinish()
            }
        }

        countDownTimer.start()
    }

    /** Set the text view to show the remaining time
     * @param totalSeconds the total seconds left
     */
    private fun setRemainingTime(totalSeconds: Int) {
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60

        timeLeft.text = String.format("%02d:%02d:%02d", h, m, s)
    }

    /** Set on finish callback - the callback to execute when Done button is clicked */
    fun setOnFinishCallback(onFinish: () -> Unit) {
        onFinishCallback = onFinish
    }
}