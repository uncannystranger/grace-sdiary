package com.grace.sdiary.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.grace.sdiary.data.local.datastore.UserPreferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DailyResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val prefs: UserPreferences
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val today = com.grace.sdiary.util.DateUtils.todayStart()
            val lastActive = prefs.getLastActiveDate()
            if (lastActive != 0L && com.grace.sdiary.util.DateUtils.daysBetween(lastActive, today) > 1) {
                prefs.setStreakCount(0)
            }
            prefs.setDailyXp(0)
            prefs.setLastActiveDate(today)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
