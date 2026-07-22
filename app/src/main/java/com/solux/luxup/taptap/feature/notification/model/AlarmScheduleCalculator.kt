package com.solux.luxup.taptap.feature.notification.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * 간격 설정의 단위. [시간] 버튼 탭 시 [분]으로, 그 반대로 자동 전환된다.
 * 수치 버튼의 조절 가능 범위는 단위마다 다르다: 시간 1~23, 분 1~90.
 */
enum class IntervalUnit(val label: String, val range: IntRange) {
    HOUR("시간", 1..23),
    MINUTE("분", 1..90);

    fun toggled(): IntervalUnit = if (this == HOUR) MINUTE else HOUR
}

/** "매 N 시간/분 마다" 간격 설정 값. */
data class AlarmInterval(val value: Int, val unit: IntervalUnit) {

    /** 수치조절 핸들 -를 눌렀을 때. */
    fun decreased(): AlarmInterval = copy(value = (value - 1).coerceIn(unit.range))

    /** 수치조절 핸들 +를 눌렀을 때. */
    fun increased(): AlarmInterval = copy(value = (value + 1).coerceIn(unit.range))

    /** 직접입력으로 수정했을 때, 현재 단위 범위로 값을 보정한다. */
    fun withValueInput(input: Int): AlarmInterval = copy(value = input.coerceIn(unit.range))

    /** [시간]/[분] 텍스트 버튼을 탭했을 때 단위를 전환하고, 값을 새 단위 범위에 맞게 보정한다. */
    fun withUnitToggled(): AlarmInterval {
        val newUnit = unit.toggled()
        return AlarmInterval(value = value.coerceIn(newUnit.range), unit = newUnit)
    }

    fun toDuration(): Duration = when (unit) {
        IntervalUnit.HOUR -> Duration.ofHours(value.toLong())
        IntervalUnit.MINUTE -> Duration.ofMinutes(value.toLong())
    }
}

/**
 * 반복 설정 on/off 결과.
 * on: 재사용 알림 - 하위 4개 버튼(매일/매주/매달/설정) 활성화 및 반복.
 * off: 일회용 알림 - 생성한 당일만 사용 후 자동 비활성화.
 */
data class RepeatSettings(val enabled: Boolean, val option: RepeatOption) {

    /** 반복 하위 옵션(매일/매주/매달/설정) 버튼 활성화 여부. */
    val subOptionsEnabled: Boolean get() = enabled

    /** 생성일 기준으로, 일회용 알림이 오늘도 유효한지 판단한다. */
    fun isActive(createdDate: LocalDate, today: LocalDate = LocalDate.now()): Boolean {
        if (enabled) return true
        return !today.isAfter(createdDate)
    }
}

private val TimeInputFormatter: DateTimeFormatter = DateTimeFormatterBuilder()
    .parseCaseInsensitive()
    .appendPattern("h:mm a")
    .toFormatter(Locale.ENGLISH)

private fun TimeRange.toLocalTimes(): Pair<LocalTime, LocalTime> {
    val start = LocalTime.parse(start, TimeInputFormatter)
    val end = LocalTime.parse(end, TimeInputFormatter)
    return start to end
}

/** 활성화 시간대 박스의 수치조절 핸들이 한 번에 조정하는 분 단위. */
const val TIME_STEP_MINUTES = 30L

/** 활성화 시간대 박스를 탭해 직접 입력했을 때, 입력한 텍스트를 시간으로 파싱한다. 형식이 올바르지 않으면 null. */
fun parseTimeInput(raw: String): LocalTime? = try {
    LocalTime.parse(raw.trim(), TimeInputFormatter)
} catch (e: DateTimeParseException) {
    null
}

/** 수치조절 핸들(-/+)로 시간을 [TIME_STEP_MINUTES]만큼 조정한다. 자정을 넘기면 자동으로 순환한다. */
fun LocalTime.stepped(forward: Boolean): LocalTime =
    if (forward) plusMinutes(TIME_STEP_MINUTES) else minusMinutes(TIME_STEP_MINUTES)

/** 활성화 시간대 박스에 표시할 텍스트로 변환한다. */
fun LocalTime.toDisplayText(): String = format(TimeInputFormatter)

/**
 * 간격 기반 알림 설정과 활성화 시간대(들)을 바탕으로,
 * 실제로 푸시 알림이 발송될 시각 목록을 계산한다.
 * 종료 시각이 시작 시각보다 앞서면(예: 밤 ~ 다음날 새벽) 익일로 넘어가는 구간으로 계산한다.
 */
fun calculateNotificationTimes(
    interval: AlarmInterval,
    activeRanges: List<TimeRange>,
    baseDate: LocalDate = LocalDate.now()
): List<LocalDateTime> {
    val duration = interval.toDuration()
    if (duration.isZero) return emptyList()

    return activeRanges.flatMap { range ->
        val (start, end) = range.toLocalTimes()
        val startAt = LocalDateTime.of(baseDate, start)
        val endAt = if (end.isAfter(start)) {
            LocalDateTime.of(baseDate, end)
        } else {
            LocalDateTime.of(baseDate.plusDays(1), end)
        }

        generateSequence(startAt) { it.plus(duration) }
            .takeWhile { !it.isAfter(endAt) }
            .toList()
    }
}