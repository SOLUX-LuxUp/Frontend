package com.solux.luxup.taptap.feature.home.buttondetail.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.solux.luxup.taptap.feature.home.buttondetail.model.ButtonRecordEntry
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

data class TimelineGroup(
    val label: String,
    val items: List<ButtonRecordEntry>
)

/** 서버가 내려주는 기록 시각은 오프셋 없이 한국 시간(Asia/Seoul) 기준이다 — "지금"도 반드시 같은 존으로 맞춰야 한다. */
private val ServiceZone: ZoneId = ZoneId.of("Asia/Seoul")

private val clockTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
private val groupDateFormatter = DateTimeFormatter.ofPattern("yy.MM.dd", Locale.KOREA)

/**
 * 서버는 오프셋 없는 LocalDateTime 문자열("2026-07-30T10:49:31[.123456]")을 내려준다.
 * 예전에는 'Z'가 붙은 UTC 형식을 기대해 파싱이 항상 실패했었다 — 홈 화면(RecentRecordTimeFormatter),
 * 팀 쪽(TeamRecentRecordBanner)과 동일하게 LocalDateTime.parse로 맞춘다.
 */
private fun parseRecordedAt(isoTimestamp: String): LocalDateTime? =
    runCatching { LocalDateTime.parse(isoTimestamp) }.getOrNull()

private fun nowInServiceZone(nowMillis: Long): LocalDateTime =
    Instant.ofEpochMilli(nowMillis).atZone(ServiceZone).toLocalDateTime()

private fun daysBetween(recordedAt: LocalDateTime, now: LocalDateTime): Long =
    ChronoUnit.DAYS.between(recordedAt.toLocalDate(), now.toLocalDate())

// 배너 접두어 / 타임라인 행 상대텍스트("오늘", "어제", "2일 전" ...)에 쓰는 날짜 label
fun formatDayLabel(isoTimestamp: String, nowMillis: Long = System.currentTimeMillis()): String {
    val recordedAt = parseRecordedAt(isoTimestamp) ?: return ""
    return when (val diffDays = daysBetween(recordedAt, nowInServiceZone(nowMillis))) {
        0L -> "오늘"
        1L -> "어제"
        else -> "${diffDays}일 전"
    }
}

// 배너 시각 뒤에 붙는 보조 텍스트. 오늘 기록에만 상대 시간("3분 전")을 붙이고, 그 외에는 표시하지 않음
fun formatBannerSuffix(isoTimestamp: String, nowMillis: Long = System.currentTimeMillis()): String {
    val recordedAt = parseRecordedAt(isoTimestamp) ?: return ""
    val diffDays = daysBetween(recordedAt, nowInServiceZone(nowMillis))
    return if (diffDays == 0L) formatRowRelativeText(isoTimestamp, nowMillis) else ""
}

// 타임라인 각 행의 좌측 상대 시간 텍스트. 오늘 기록만 분/시간 단위로, 그 외에는 날짜 label과 동일하게 표기
fun formatRowRelativeText(isoTimestamp: String, nowMillis: Long = System.currentTimeMillis()): String {
    val recordedAt = parseRecordedAt(isoTimestamp) ?: return ""
    val now = nowInServiceZone(nowMillis)
    val diffDays = daysBetween(recordedAt, now)
    if (diffDays != 0L) return formatDayLabel(isoTimestamp, nowMillis)

    val diffMinutes = Duration.between(recordedAt, now).toMinutes().coerceAtLeast(0)
    return when {
        diffMinutes < 1 -> "방금 전"
        diffMinutes < 60 -> "${diffMinutes}분 전"
        else -> "${diffMinutes / 60}시간 전"
    }
}

// 타임라인 행 / 배너에 쓰는 "11:37 AM" 형태의 시각 텍스트
fun formatRecordedAtDisplay(isoTimestamp: String): String {
    val recordedAt = parseRecordedAt(isoTimestamp) ?: return ""
    return clockTimeFormatter.format(recordedAt)
}

// 최근 기록 배너의 날짜 접두어("오늘", "어제")
fun formatBannerDayPrefix(isoTimestamp: String, nowMillis: Long = System.currentTimeMillis()): String =
    formatDayLabel(isoTimestamp, nowMillis)

// 타임라인 그룹 헤더에 쓰는 "26.07.21" 형태의 날짜 label
fun formatGroupDateLabel(isoTimestamp: String): String {
    val recordedAt = parseRecordedAt(isoTimestamp) ?: return ""
    return groupDateFormatter.format(recordedAt)
}

// recordedAt 기준 최신순으로 정렬한 뒤, 같은 날짜끼리 묶어 타임라인 그룹 리스트로 변환
fun groupRecordsByDay(records: List<ButtonRecordEntry>): List<TimelineGroup> =
    records
        .sortedByDescending { parseRecordedAt(it.recordedAt) ?: LocalDateTime.MIN }
        .groupBy { formatGroupDateLabel(it.recordedAt) }
        .map { (label, items) -> TimelineGroup(label, items) }

/**
 * "방금 전" / "N분 전" 같은 경과 시간 텍스트는 계산된 시점에 recomposition이 없으면 화면에 그대로 멈춰 있는다.
 * 일정 주기로 현재 시각을 다시 읽어 recomposition을 유발해, 화면을 나갔다 들어오지 않아도 계속 갱신되게 한다.
 */
@Composable
fun rememberTickingNowMillis(intervalMillis: Long = 30_000L): State<Long> =
    produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            delay(intervalMillis)
            value = System.currentTimeMillis()
        }
    }