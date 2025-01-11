package com.example.fitnessapp.managers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.fitnessapp.BaseActivity
import com.example.fitnessapp.R

/** Notification manager to send notifications to the user */
class CustomNotificationManager(ctx: BaseActivity, tId: Int, mId: Int) {
    private var activity = ctx
    private var titleId = tId
    private var messageId = mId
    private var channelId = "timer_notification_channel"

    /** Send a notification when the timer finishes */
    fun sendNotification() {
        val notificationManager = activity.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Create an Intent to open the app when the notification is clicked
        val intent = Intent(activity, activity::class.java) // Replace YourActivity with your launcher activity
        intent.action = Intent.ACTION_MAIN
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED

        val pendingIntent = PendingIntent.getActivity(activity, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Create the notification
        val notification = NotificationCompat.Builder(activity, channelId)
            .setContentTitle(activity.getString(titleId))
            .setContentText(activity.getString(messageId))
            .setSmallIcon(R.drawable.icon_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Send the notification
        notificationManager.notify(1, notification)
    }
}