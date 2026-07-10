package com.grace.sdiary.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.grace.sdiary.data.local.db.dao.ReminderDao
import com.grace.sdiary.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ReminderCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val reminderDao: ReminderDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val now = System.currentTimeMillis()
            val overdueReminders = reminderDao.getOverdueSync(now)
            overdueReminders.forEach { reminder ->
                NotificationHelper.showReminderNotification(applicationContext, reminder)
                // Reschedule if repeating
                if (reminder.repeatInterval != null && reminder.repeatInterval != "none") {
                    val nextTime = calculateNextTime(reminder.dateTime, reminder.repeatInterval)
                    reminderDao.update(reminder.copy(dateTime = nextTime))
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e("ReminderCheckWorker", "Error checking reminders", e)
            Result.retry()
        }
    }

    private fun calculateNextTime(currentTime: Long, interval: String): Long {
        return when (interval) {
            "daily" -> currentTime + 86400000L
            "weekly" -> currentTime + 604800000L
            "monthly" -> currentTime + 2592000000L
            "yearly" -> currentTime + 31536000000L
            else -> currentTime + 86400000L
        }
    }
}
