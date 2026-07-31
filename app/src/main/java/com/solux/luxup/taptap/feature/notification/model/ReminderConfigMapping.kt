package com.solux.luxup.taptap.feature.notification.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter

// 백엔드가 주고받는 LocalTime은 "HH:mm:ss" 문자열, 화면 입력 위젯(TimePill)은 "h:mm a" 문자열을 쓴다.
private val BackendTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

fun String.backendTimeToDisplay(): String =
    runCatching { LocalTime.parse(this, BackendTimeFormatter).toDisplayText() }.getOrElse { this }

fun String.displayTimeToBackend(): String =
    (parseTimeInput(this) ?: LocalTime.NOON).format(BackendTimeFormatter)

// 백엔드 daysOfWeek는 1(월)~7(일), 화면(WeekdayLabels)은 인덱스 0(월)~6(일)을 쓴다.
fun List<Int>.toWeekdayIndices(): Set<Int> = map { it - 1 }.toSet()

fun Set<Int>.toBackendDaysOfWeek(): List<Int> = map { it + 1 }.sorted()