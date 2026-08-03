package com.solux.luxup.taptap.feature.insight.monthly.util

import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val IsoDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

/** 서버가 내려주는 시각은 오프셋 없이 한국 시간(Asia/Seoul) 기준이다 — "이번 달"도 반드시 같은 존으로 맞춰야 한다. */
private val ServiceZone: ZoneId = ZoneId.of("Asia/Seoul")

/** (year, month) → "2026년 5월" */
fun monthNavLabel(year: Int, month: Int): String = "${year}년 ${month}월"

/** 월 이동 (prev/next) — months 단위, 연도 넘어가는 것도 처리 */
fun shiftMonth(year: Int, month: Int, months: Long): Pair<Int, Int> {
    val shifted = YearMonth.of(year, month).plusMonths(months)
    return shifted.year to shifted.monthValue
}

/** (year, month)가 이번 달보다 미래인지 여부 — 기록이 존재할 수 없어 API 조회 없이 빈 상태로 처리한다 */
fun isFutureMonth(year: Int, month: Int): Boolean =
    YearMonth.of(year, month).isAfter(YearMonth.now(ServiceZone))

/** (year, month)가 이번 달보다 이전(과거)인지 여부 */
fun isPastMonth(year: Int, month: Int): Boolean =
    YearMonth.of(year, month).isBefore(YearMonth.now(ServiceZone))

/** "yyyy-MM-dd"(busiestDay) → "12일" */
fun String.toKoreanDayText(): String =
    try {
        "${LocalDate.parse(this, IsoDateFormatter).dayOfMonth}일"
    } catch (_: Exception) {
        this
    }