package com.solux.luxup.taptap.feature.insight.monthly.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

private val IsoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

/** (year, month) → "2026년 5월" */
fun monthNavLabel(year: Int, month: Int): String = "${year}년 ${month}월"

/** 월 이동 (prev/next) — months 단위, 연도 넘어가는 것도 처리 */
fun shiftMonth(year: Int, month: Int, months: Long): Pair<Int, Int> {
    val shifted = YearMonth.of(year, month).plusMonths(months)
    return shifted.year to shifted.monthValue
}

/** "yyyy-MM-dd"(busiestDay) → "12일" */
fun String.toKoreanDayText(): String =
    try {
        "${LocalDate.parse(this, IsoDateFormatter).dayOfMonth}일"
    } catch (_: Exception) {
        this
    }