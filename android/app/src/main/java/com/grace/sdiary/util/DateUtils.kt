package com.grace.sdiary.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

object DateUtils {

    fun now(): Long = System.currentTimeMillis()

    fun todayStart(): Long {
        return LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun todayEnd(): Long {
        return LocalDate.now()
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - 1
    }

    fun weekStart(): Long {
        return LocalDate.now()
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun monthStart(): Long {
        return LocalDate.now()
            .withDayOfMonth(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun monthEnd(): Long {
        return LocalDate.now()
            .withDayOfMonth(1)
            .plusMonths(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - 1
    }

    fun yearStart(): Long {
        return LocalDate.now()
            .withDayOfYear(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun formatDate(timestamp: Long, pattern: String = "MMM dd, yyyy"): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return toLocalDateTime(timestamp).format(formatter)
    }

    fun formatTime(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return toLocalDateTime(timestamp).format(formatter)
    }

    fun formatDateTime(timestamp: Long): String {
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a")
        return toLocalDateTime(timestamp).format(formatter)
    }

    fun relativeTime(timestamp: Long): String {
        val now = LocalDateTime.now()
        val then = toLocalDateTime(timestamp)
        val diffMinutes = ChronoUnit.MINUTES.between(then, now)

        return when {
            diffMinutes < 1 -> "Just now"
            diffMinutes < 60 -> "${diffMinutes}m ago"
            diffMinutes < 1440 -> "${diffMinutes / 60}h ago"
            diffMinutes < 2880 -> "Yesterday"
            diffMinutes < 60480 -> "${diffMinutes / 1440}d ago"
            else -> formatDate(timestamp)
        }
    }

    fun isToday(timestamp: Long): Boolean {
        return toLocalDate(timestamp) == LocalDate.now()
    }

    fun isYesterday(timestamp: Long): Boolean {
        return toLocalDate(timestamp) == LocalDate.now().minusDays(1)
    }

    fun isSameDay(t1: Long, t2: Long): Boolean {
        return toLocalDate(t1) == toLocalDate(t2)
    }

    fun daysBetween(from: Long, to: Long): Int {
        return ChronoUnit.DAYS.between(toLocalDate(from), toLocalDate(to)).toInt()
    }

    fun getMonthName(month: Int): String {
        val m = Month.of(month.coerceIn(1, 12))
        return m.name.lowercase().replaceFirstChar { it.uppercase() }
    }

    fun getDayName(dayOfWeek: Int): String {
        val day = DayOfWeek.of(dayOfWeek.coerceIn(1, 7))
        return day.name.lowercase().replaceFirstChar { it.uppercase() }
    }

    fun startOfDay(timestamp: Long): Long {
        return toLocalDate(timestamp)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun endOfDay(timestamp: Long): Long {
        return toLocalDate(timestamp)
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() - 1
    }

    fun toLocalDate(timestamp: Long): LocalDate {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    private fun toLocalDateTime(timestamp: Long): LocalDateTime {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }
}
