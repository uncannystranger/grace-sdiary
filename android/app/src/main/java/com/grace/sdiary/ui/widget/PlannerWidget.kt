package com.grace.sdiary.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.grace.sdiary.MainActivity
import com.grace.sdiary.R
import com.grace.sdiary.data.local.db.AppDatabase
import com.grace.sdiary.util.DateUtils

class PlannerWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_planner)

            // Open app on tap
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

            // Load today's tasks from DB (on main thread for widget simplicity)
            try {
                val db = AppDatabase.getInstance(context)
                val todayStart = DateUtils.todayStart()
                val todayEnd = DateUtils.todayEnd()
                val allItems = db.plannerDao().getAllSync()
                val items = allItems.filter { it.date in todayStart..todayEnd }
                val done = items.count { it.isComplete }
                val total = items.size
                views.setTextViewText(R.id.widget_title, "Today's Planner")
                views.setTextViewText(R.id.widget_subtitle, "$done / $total tasks")
                views.setTextViewText(R.id.widget_progress, if (total > 0) "${(done * 100 / total)}%" else "0%")
                val sb = StringBuilder()
                items.take(5).forEachIndexed { i, item ->
                    val check = if (item.isComplete) "\u2713 " else "\u25CB "
                    sb.appendLine("$check${item.title}")
                }
                if (items.isEmpty()) sb.append("No tasks for today")
                views.setTextViewText(R.id.widget_list, sb.toString().trimEnd())
            } catch (e: Exception) {
                views.setTextViewText(R.id.widget_title, "Grace's Diary")
                views.setTextViewText(R.id.widget_subtitle, "Open app to view planner")
                views.setTextViewText(R.id.widget_list, "Tap to open")
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
