package com.solux.luxup.taptap.feature.insight.daily.util

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ClockFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
private val TargetDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

/** ISO 문자열 → "10:43 AM" */
fun String.toClockText(): String =
    try {
        LocalDateTime.parse(this).format(ClockFormatter)
    } catch (_: Exception) {
        "-"
    }

/** ISO 문자열 → "3시간 전" (화면 열린 시점 기준 FE 계산) */
fun String.toElapsedText(): String =
    try {
        val minutes = Duration.between(LocalDateTime.parse(this), LocalDateTime.now()).toMinutes()
        when {
            minutes < 1 -> "방금 전"
            minutes < 60 -> "${minutes}분 전"
            minutes < 60 * 24 -> "${minutes / 60}시간 전"
            else -> "${minutes / (60 * 24)}일 전"
        }
    } catch (_: Exception) {
        ""
    }

/** "yyyy-MM-dd" → "2026년 5월 3일" */
fun String.toKoreanDateText(): String =
    try {
        val date = LocalDate.parse(this, TargetDateFormatter)
        "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일"
    } catch (_: Exception) {
        this
    }

/** 날짜 이동 (prev/next) — "yyyy-MM-dd" 기준 */
fun String.shiftDate(days: Long): String =
    try {
        LocalDate.parse(this, TargetDateFormatter).plusDays(days).format(TargetDateFormatter)
    } catch (_: Exception) {
        this
    }