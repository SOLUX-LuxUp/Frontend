package com.solux.luxup.taptap.core.util

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** ISO-8601(예: "2025-05-23T14:32:00") → "20분 전". 카드·배너 공통. */
fun formatTimeAgo(isoDateTime: String): String {
    val recordedAt = runCatching {
        LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }.getOrNull() ?: return ""

    val minutes = Duration.between(recordedAt, LocalDateTime.now()).toMinutes()
    return when {
        minutes < 1              -> "방금 전"
        minutes < 60             -> "${minutes}분 전"
        minutes < 60 * 24        -> "${minutes / 60}시간 전"
        minutes < 60 * 24 * 7    -> "${minutes / (60 * 24)}일 전"
        else -> recordedAt.format(DateTimeFormatter.ofPattern("M월 d일"))
    }
}

/** ISO-8601 → "11:41 AM". 카드·배너 공통. */
fun formatTimeOfDay(isoDateTime: String): String {
    val recordedAt = runCatching {
        LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }.getOrNull() ?: return ""

    return recordedAt.format(DateTimeFormatter.ofPattern("h:mm a", java.util.Locale.ENGLISH))
}