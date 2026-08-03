package com.solux.luxup.taptap.feature.insight.daily.util

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ClockFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
private val TargetDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

/** 서버가 내려주는 시각은 오프셋 없이 한국 시간(Asia/Seoul) 기준이다 — "지금"도 반드시 같은 존으로 맞춰야 한다. */
private val ServiceZone: ZoneId = ZoneId.of("Asia/Seoul")

/** ISO 문자열 → "10:43 AM" */
fun String.toClockText(): String =
    try {
        LocalDateTime.parse(this).format(ClockFormatter)
    } catch (_: Exception) {
        "-"
    }

/**
 * ISO 문자열 → "3시간 전" (FE 계산).
 * [now]를 넘기지 않으면 호출 시점 한 번만 계산되어 화면을 계속 띄워놔도 "방금 전"에 멈춰있을 수 있다 —
 * 화면이 열려있는 동안 갱신되길 원하면 [rememberTickingNow]로 만든 값을 넘긴다.
 */
fun String.toElapsedText(now: LocalDateTime = LocalDateTime.now(ServiceZone)): String =
    try {
        // 초 단위 클럭 오차로 방금 남긴 기록이 잠깐 "미래"처럼 보이는 정도는 0분으로 눙친다.
        val minutes = Duration.between(LocalDateTime.parse(this), now).toMinutes().coerceAtLeast(0)
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

/** "yyyy-MM-dd" 가 오늘(한국 시간 기준)보다 이전(과거)인지 여부 */
fun String.isPastDate(): Boolean =
    try {
        LocalDate.parse(this, TargetDateFormatter).isBefore(LocalDate.now(ServiceZone))
    } catch (_: Exception) {
        false
    }

/** "yyyy-MM-dd" 가 오늘(한국 시간 기준)보다 이후(미래)인지 여부 — 기록이 존재할 수 없어 API 조회 없이 빈 상태로 처리한다 */
fun String.isFutureDate(): Boolean =
    try {
        LocalDate.parse(this, TargetDateFormatter).isAfter(LocalDate.now(ServiceZone))
    } catch (_: Exception) {
        false
    }