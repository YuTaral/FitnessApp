package com.example.fitnessapp.managers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.fitnessapp.R

/** Notification manager to send notifications to the user */
class CustomNotificationManager(ctx: Context, tId: Int, mId: Int) {
    private var context = ctx
    private var titleId = tId
    private var messageId = mId

    /** Send a notification when the timer finishes */
    fun sendNotification() {
        val notificationManager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "timer_notification_channel"

        // Create notification channel for Android 8.0 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Timer Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(titleId))
            .setContentText(context.getString(messageId))
            .setSmallIcon(R.drawable.icon_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Send the notification
        notificationManager.notify(1, notification)
    }
}