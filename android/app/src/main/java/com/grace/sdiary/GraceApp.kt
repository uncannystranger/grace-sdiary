package com.grace.sdiary

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.grace.sdiary.util.Constants
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class GraceApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        schedulePeriodicWork()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val channels = listOf(
                NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_REMINDERS,
                    "Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Study and task reminders"
                    enableVibration(true)
                    setShowBadge(true)
                },
                NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_STUDY,
                    "Study Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Vocabulary and learning reminders"
                },
                NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_WATER,
                    "Health Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Drink water and health reminders"
                },
                NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_DIARY,
                    "Diary Reminders",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Daily diary writing reminders"
                },
                NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_GENERAL,
                    "General Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "App updates and general notifications"
                }
            )

            channels.forEach { manager.createNotificationChannel(it) }
        }
    }

    private fun schedulePeriodicWork() {
        val workManager = WorkManager.getInstance(this)

        val dailyReset = PeriodicWorkRequestBuilder<com.grace.sdiary.worker.DailyResetWorker>(
            24, TimeUnit.HOURS
        ).setConstraints(
            Constraints.Builder().setRequiresBatteryNotLow(true).build()
        ).build()

        workManager.enqueueUniquePeriodicWork(
            Constants.WORK_NAME_RESET_DAILY,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyReset
        )

        val reminderCheck = PeriodicWorkRequestBuilder<com.grace.sdiary.worker.ReminderCheckWorker>(
            15, TimeUnit.MINUTES
        ).build()

        workManager.enqueueUniquePeriodicWork(
            Constants.WORK_NAME_CHECK_REMINDERS,
            ExistingPeriodicWorkPolicy.KEEP,
            reminderCheck
        )
    }
}
