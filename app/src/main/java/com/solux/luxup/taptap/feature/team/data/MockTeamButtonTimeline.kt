package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.MemberProfile
import com.solux.luxup.taptap.feature.team.model.TeamButtonLatest
import com.solux.luxup.taptap.feature.team.model.TeamButtonTimeline
import com.solux.luxup.taptap.feature.team.model.TeamButtonTimelineRecord
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 타임라인 목데이터. 실 API 연동 시 삭제 대상.
 * 오늘/어제 그룹이 나뉘도록 현재 시각 기준으로 만든다.
 */
object MockTeamButtonTimeline {

    private val nuri = MemberProfile(1L, "누리", null)
    private val heekyung = MemberProfile(2L, "희경", null)

    private val now: LocalDateTime = LocalDateTime.now()

    private fun at(minusHours: Long, minusDays: Long = 0): String =
        now.minusDays(minusDays)
            .minusHours(minusHours)
            .withSecond(0)
            .withNano(0)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))

    val records = listOf(
        TeamButtonTimelineRecord(10L, at(0), null, null, nuri),
        TeamButtonTimelineRecord(9L, at(2), "회의 내용 반영", "📝", nuri),
        TeamButtonTimelineRecord(8L, at(1, 1), null, null, heekyung),
        TeamButtonTimelineRecord(7L, at(3, 1), null, null, nuri),
        TeamButtonTimelineRecord(6L, at(5, 1), null, "🔥", heekyung),
        TeamButtonTimelineRecord(5L, at(7, 1), null, null, nuri),
    )

    val timeline = TeamButtonTimeline(
        records = records,
        hasMore = true,
        nextCursor = 5L,
    )

    val latest = TeamButtonLatest(
        teamButtonId = 1L,
        buttonName = "기획서 업데이트",
        iconName = "document",
        iconColor = "yellow",
        latestRecord = records.first(),
    )
}