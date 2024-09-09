package com.example.pintask.appfunctions

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import com.example.pintask.R
import com.example.pintask.TaskDetailActivity
import com.example.pintask.constants.AppConstants

object NotificationFunctions {
    fun removeFromNotification(context: Context, notificationID: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationID)
    }


    private const val channel_ID = "i.apps.notifications"
    private const val description = "Test notification"

    fun buildNotification(context: Context, taskID: String, taskTitle: String, task: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, TaskDetailActivity::class.java)
        intent.putExtra(AppConstants.KEY_TASK_ID, taskID)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingIntent = PendingIntent.getActivity(
            context,
            taskID.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Use these flags for stability and to ensure the correct behavior.
        )

        // Ensure notification channel exists for API >= 26
        val notificationChannel =
            NotificationChannel(channel_ID, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationBuilder = Notification.Builder(context, channel_ID)
            .setSmallIcon(R.drawable.pushpin_selected)
            .setContentTitle(taskTitle)
            .setContentText(task)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // Ensures notification stays in the status bar
            .setOnlyAlertOnce(true)
            .setStyle(Notification.BigTextStyle().bigText(task)) // Expandable notification

        notificationManager.notify(taskID.toInt(), notificationBuilder.build())
    }


}