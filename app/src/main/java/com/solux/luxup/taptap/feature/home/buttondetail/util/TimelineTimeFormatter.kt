package com.solux.luxup.taptap.feature.home.buttondetail.util

import com.solux.luxup.taptap.feature.home.buttondetail.model.ButtonRecordEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class TimelineGroup(
    val label: String,
    val items: List<ButtonRecordEntry>
)

private val isoUtcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("UTC")
}
private val clockTimeFormat = SimpleDateFormat("h:mm a", Locale.US)
private val groupDateFormat = SimpleDateFormat("yy.MM.dd", Locale.KOREA)

private fun parseIsoUtcMillis(isoTimestamp: String): Long? =
    runCatching { isoUtcFormat.parse(isoTimestamp)?.time }.getOrNull()

private fun startOfDayMillis(millis: Long): Long =
    Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

private fun daysBetween(fromMillis: Long, toMillis: Long): Int =
    ((startOfDayMillis(toMillis) - startOfDayMillis(fromMillis)) / (24 * 60 * 60 * 1000L)).toInt()

// 배너 접두어 / 타임라인 행 상대텍스트("오늘", "어제", "2일 전" ...)에 쓰는 날짜 label
fun formatDayLabel(isoTimestamp: String, nowMillis: Long = System.currentTimeMillis()): String {
    val recordedMillis = parseIsoUtcMillis(isoTimestamp) ?: return ""
    return when (val diffDays = daysBetween(recordedMillis, nowMillis)) {
        0 -> "오늘"
        1 -> "어제"
        else -> "${diffDays}일 전"
    }
}

// 배너 시각 뒤에 붙는 보조 텍스트. 오늘 기록에만 상대 시간("3분 전")을 붙이고, 그 외에는 표시하지 않음
fun formatBannerSuffix(isoTimestamp: String, nowMillis: Long = System.currentTimeMillis()): String {
    val recordedMillis = parseIsoUtcMillis(isoTimestamp) ?: return ""
    val diffDays = daysBetween(recordedMillis, nowMillis)
    return if (diffDays == 0) formatRowRelativeText(isoTimestamp, nowMillis) else ""
}

// 타임라인 각 행의 좌측 상대 시간 텍스트. 오늘 기록만 분/시간 단위로, 그 외에는 날짜 label과 동일하게 표기
fun formatRowRelativeText(isoTimestamp: String, nowMillis: Long = System.currentTimeMillis()): String {
    val recordedMillis = parseIsoUtcMillis(isoTimestamp) ?: return ""
    val diffDays = daysBetween(recordedMillis, nowMillis)
    if (diffDays != 0) return formatDayLabel(isoTimestamp, nowMillis)

    val diffMinutes = ((nowMillis - recordedMillis) / 60_000L).coerceAtLeast(0)
    return when {
        diffMinutes < 1 -> "방금 전"
        diffMinutes < 60 -> "${diffMinutes}분 전"
        else -> "${diffMinutes / 60}시간 전"
    }
}

// 타임라인 행 / 배너에 쓰는 "11:37 AM" 형태의 시각 텍스트
fun formatRecordedAtDisplay(isoTimestamp: String): String {
    val recordedMillis = parseIsoUtcMillis(isoTimestamp) ?: return ""
    return clockTimeFormat.format(Date(recordedMillis))
}

// 최근 기록 배너의 날짜 접두어("오늘", "어제")
fun formatBannerDayPrefix(isoTimestamp: String, nowMillis: Long = System.currentTimeMillis()): String =
    formatDayLabel(isoTimestamp, nowMillis)

// 타임라인 그룹 헤더에 쓰는 "26.07.21" 형태의 날짜 label
fun formatGroupDateLabel(isoTimestamp: String): String {
    val recordedMillis = parseIsoUtcMillis(isoTimestamp) ?: return ""
    return groupDateFormat.format(Date(recordedMillis))
}

// recordedAt 기준 최신순으로 정렬한 뒤, 같은 날짜끼리 묶어 타임라인 그룹 리스트로 변환
fun groupRecordsByDay(records: List<ButtonRecordEntry>): List<TimelineGroup> =
    records
        .sortedByDescending { parseIsoUtcMillis(it.recordedAt) ?: 0L }
        .groupBy { formatGroupDateLabel(it.recordedAt) }
        .map { (label, items) -> TimelineGroup(label, items) }