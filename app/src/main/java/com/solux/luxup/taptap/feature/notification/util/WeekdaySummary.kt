package com.solux.luxup.taptap.feature.notification.util

/** 월~일 한 글자 표기. 인덱스는 0(월)~6(일). */
val WeekdayLabels = listOf("월", "화", "수", "목", "금", "토", "일")

/**
 * 선택된 요일(인덱스 0~6)을 다음 규칙으로 요약한다.
 * 1개: "월요일" / 2개: "월, 화" / 3개 이상 비연속: "월, 수, 금" /
 * 3개 이상 연속: "월~수" / 혼합: "월~수, 금, 일"
 */
fun formatWeekdaySummary(selected: Set<Int>): String {
    if (selected.isEmpty()) return ""
    if (selected.size == 1) return "${WeekdayLabels[selected.first()]}요일"

    val sorted = selected.sorted()
    val runs = mutableListOf<MutableList<Int>>()
    for (day in sorted) {
        val lastRun = runs.lastOrNull()
        if (lastRun != null && day == lastRun.last() + 1) {
            lastRun += day
        } else {
            runs += mutableListOf(day)
        }
    }

    return runs.joinToString(", ") { run ->
        if (run.size >= 3) {
            "${WeekdayLabels[run.first()]}~${WeekdayLabels[run.last()]}"
        } else {
            run.joinToString(", ") { WeekdayLabels[it] }
        }
    }
}