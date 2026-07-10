package com.grace.sdiary.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.grace.sdiary.MainActivity
import com.grace.sdiary.R
import com.grace.sdiary.data.local.db.entity.ReminderEntity
import com.grace.sdiary.util.Constants

object NotificationHelper {

    private var notificationId = 1000

    fun showReminderNotification(context: Context, reminder: ReminderEntity) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            reminder.deepLink?.let { putExtra("deep_link", it) }
        }
        val pendingIntent = PendingIntent.getActivity(
            context, reminder.id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = when (reminder.type) {
            "study" -> Constants.NOTIFICATION_CHANNEL_STUDY
            "water" -> Constants.NOTIFICATION_CHANNEL_WATER
            "diary" -> Constants.NOTIFICATION_CHANNEL_DIARY
            else -> Constants.NOTIFICATION_CHANNEL_REMINDERS
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(reminder.title)
            .setContentText(reminder.description ?: "Time for your reminder!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        reminder.description?.let {
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(it))
        }

        try {
            NotificationManagerCompat.from(context).notify(reminder.notificationId, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun showScheduledNotification(context: Context, title: String, message: String, channelId: String = Constants.NOTIFICATION_CHANNEL_GENERAL) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationId++
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (_: SecurityException) {}
    }
}
