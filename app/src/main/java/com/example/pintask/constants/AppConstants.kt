package com.example.pintask.constants

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.pintask.R
import com.example.pintask.TaskDetailActivity
import com.example.pintask.datastore.PreferenceStore

object AppConstants {
    const val DEFAULT_TASK_TITLE = "Untitled task"
    const val DEFAULT_TASK_DESC = ""
    const val DEFAULT_PINNED_VALUE = false
    const val KEY_TASK_ID = "TASK_ID"

    fun notifyUser(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun removeFromNotification(context: Context, notificationID: Int) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationID)
    }


    private const val channel_ID = "i.apps.notifications"
    private const val description = "Test notification"

    fun buildNotification(context: Context,taskID: String, taskTitle: String, task: String){
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, TaskDetailActivity::class.java)
        intent.putExtra(KEY_TASK_ID, taskID)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // existing PendingIntent is canceled(CANCEL_CURRENT)
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                taskID.toInt(),
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
            )

        //api>=26 requires notification channel
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
            .setOngoing(true) // to keep notification in notification bar
            .setOnlyAlertOnce(true)
            .setStyle(Notification.BigTextStyle().bigText(task)) // expandable notification

        notificationManager.notify(taskID.toInt(), notificationBuilder.build())
    }


}