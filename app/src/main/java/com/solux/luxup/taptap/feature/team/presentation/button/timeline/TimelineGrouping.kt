package com.solux.luxup.taptap.feature.team.presentation.button.timeline

import com.solux.luxup.taptap.feature.team.model.TeamButtonTimelineRecord
import com.solux.luxup.taptap.feature.team.model.TimelineDayGroup
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 기록을 일자별로 묶는다. 서버가 최신순으로 주므로 순서를 그대로 유지한다.
 * 라벨은 오늘 / 어제 / M월 d일.
 */
fun groupTeamRecordsByDay(records: List<TeamButtonTimelineRecord>): List<TimelineDayGroup> {
    val today = LocalDate.now()

    return records
        .groupBy { runCatching { LocalDateTime.parse(it.recordedAt).toLocalDate() }.getOrNull() }
        .map { (date, items) ->
            TimelineDayGroup(
                label = when (date) {
                    null -> "기타"
                    today -> "오늘"
                    today.minusDays(1) -> "어제"
                    else -> "${date.monthValue}월 ${date.dayOfMonth}일"
                },
                records = items,
            )
        }
}