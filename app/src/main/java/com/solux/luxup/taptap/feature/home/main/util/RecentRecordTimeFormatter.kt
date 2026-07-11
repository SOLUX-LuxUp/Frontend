package com.solux.luxup.taptap.feature.home.main.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val isoUtcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}
private val displayTimeFormat = SimpleDateFormat("a h:mm", Locale.US)

private fun parseIsoUtcMillis(isoTimestamp: String): Long? =
    runCatching { isoUtcFormat.parse(isoTimestamp)?.time }.getOrNull()

fun formatElapsedText(isoTimestamp: String, nowMillis: Long = System.currentTimeMillis()): String {
    val recordedMillis = parseIsoUtcMillis(isoTimestamp) ?: return ""
    val diffMinutes = ((nowMillis - recordedMillis) / 60_000L).coerceAtLeast(0)
    return when {
        diffMinutes < 1 -> "방금 전"
        diffMinutes < 60 -> "${diffMinutes}분 전"
        diffMinutes < 60 * 24 -> "${diffMinutes / 60}시간 전"
        else -> "${diffMinutes / (60 * 24)}일 전"
    }
}

fun formatRecordedAtText(isoTimestamp: String): String {
    val recordedMillis = parseIsoUtcMillis(isoTimestamp) ?: return ""
    return displayTimeFormat.format(Date(recordedMillis))
}