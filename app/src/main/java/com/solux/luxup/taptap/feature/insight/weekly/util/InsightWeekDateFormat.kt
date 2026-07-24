package com.solux.luxup.taptap.feature.insight.weekly.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

private val WeekStartFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

/** "yyyy-MM-dd"(weekStart) → "2026년 5월" */
fun String.toKoreanMonthText(): String =
    try {
        val date = LocalDate.parse(this, WeekStartFormatter)
        "${date.year}년 ${date.monthValue}월"
    } catch (_: Exception) {
        this
    }

/** "yyyy-MM-dd"(weekStart) → "2주차" (해당 월 기준 몇째 주인지) */
fun String.toWeekOfMonthText(): String =
    try {
        val date = LocalDate.parse(this, WeekStartFormatter)
        val weekOfMonth = ceil(date.dayOfMonth / 7.0).toInt()
        "${weekOfMonth}주차"
    } catch (_: Exception) {
        this
    }

/** 주간 이동 (prev/next) — weekStart 기준 7일 단위 */
fun String.shiftWeek(weeks: Long): String =
    try {
        LocalDate.parse(this, WeekStartFormatter).plusWeeks(weeks).format(WeekStartFormatter)
    } catch (_: Exception) {
        this
    }