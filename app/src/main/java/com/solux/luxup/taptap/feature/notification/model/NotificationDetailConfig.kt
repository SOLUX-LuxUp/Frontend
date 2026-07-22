package com.solux.luxup.taptap.feature.notification.model

enum class RepeatOption(val label: String) {
    DAILY("매일"),
    WEEKLY("매주"),
    MONTHLY("매달"),
    CUSTOM("설정")
}

enum class AlarmMode(val label: String) {
    INTERVAL("간격 기반"),
    TIME("시간 기반")
}

data class TimeRange(val start: String, val end: String)

/**
 * "설정"(CUSTOM) 반복의 단위. [주] 버튼 탭 시 [달]로, 그 반대로 자동 전환된다.
 * 수치 버튼의 조절 가능 범위는 단위마다 다르다: 주 1~10, 달 1~12.
 */
enum class CustomRepeatUnit(val label: String, val range: IntRange) {
    WEEK("주", 1..10),
    MONTH("달", 1..12);

    fun toggled(): CustomRepeatUnit = if (this == WEEK) MONTH else WEEK
}

/** "매 N 주/달 마다 [요일]" 커스텀 반복 설정 값. */
data class CustomRepeatInterval(val value: Int, val unit: CustomRepeatUnit) {

    fun decreased(): CustomRepeatInterval = copy(value = (value - 1).coerceIn(unit.range))

    fun increased(): CustomRepeatInterval = copy(value = (value + 1).coerceIn(unit.range))

    fun withValueInput(input: Int): CustomRepeatInterval = copy(value = input.coerceIn(unit.range))

    fun withUnitToggled(): CustomRepeatInterval {
        val newUnit = unit.toggled()
        return CustomRepeatInterval(value = value.coerceIn(newUnit.range), unit = newUnit)
    }
}