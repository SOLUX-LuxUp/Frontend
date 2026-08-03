package com.solux.luxup.taptap.feature.insight.weekly.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.ceil

private val WeekStartFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

/** 서버가 내려주는 시각은 오프셋 없이 한국 시간(Asia/Seoul) 기준이다 — "이번 주"도 반드시 같은 존으로 맞춰야 한다. */
private val ServiceZone: ZoneId = ZoneId.of("Asia/Seoul")

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

/** weekStart("yyyy-MM-dd")가 이번 주보다 미래인지 여부 — 기록이 존재할 수 없어 API 조회 없이 빈 상태로 처리한다 */
fun String.isFutureWeek(): Boolean =
    try {
        LocalDate.parse(this, WeekStartFormatter).isAfter(currentWeekStart())
    } catch (_: Exception) {
        false
    }

/** weekStart("yyyy-MM-dd")가 이번 주보다 이전(과거)인지 여부 */
fun String.isPastWeek(): Boolean =
    try {
        LocalDate.parse(this, WeekStartFormatter).isBefore(currentWeekStart())
    } catch (_: Exception) {
        false
    }

private fun currentWeekStart(): LocalDate {
    val today = LocalDate.now(ServiceZone)
    return today.minusDays((today.dayOfWeek.value - DayOfWeek.MONDAY.value).toLong())
}