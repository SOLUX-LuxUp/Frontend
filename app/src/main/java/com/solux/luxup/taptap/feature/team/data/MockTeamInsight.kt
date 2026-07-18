package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.TeamInsightButtonCount
import com.solux.luxup.taptap.feature.team.model.TeamInsightDaily
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberActivity
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberTopButton
import com.solux.luxup.taptap.feature.team.model.TeamInsightTappedMember
import com.solux.luxup.taptap.feature.team.model.TeamInsightTopButton
import com.solux.luxup.taptap.feature.team.model.TeamInsightTimelineItem
import java.time.LocalDateTime
/**
 * 프리뷰·개발용 목데이터 (API 연결 전) — 실제 8.1.8 응답 형태에 맞춤.
 * 요청 대기 필드(icon/category/tappedMembers)는 "요청 반영 후 완성형"으로 채워 UI 검증.
 * 버튼 축 30+19+10+8+1 = 68 / 팀원 축 28+22+18 = 68
 */
val MockTeamInsightDaily = TeamInsightDaily(
    teamId = 1L,
    targetDate = "2025-05-23",
    totalTapCount = 68,
    topButton = TeamInsightTopButton(
        teamButtonId = 1L,
        buttonName = "출석",
        iconName = "doc",
        iconColor = "#FFC94C",
        tapCount = 7,
        tappedMembers = listOf(
            TeamInsightTappedMember(4L, "유하연", null),
            TeamInsightTappedMember(7L, "정수민", null),
            TeamInsightTappedMember(9L, "김민지", null),
            TeamInsightTappedMember(2L, "누리", null),
            TeamInsightTappedMember(3L, "희경", null),
            TeamInsightTappedMember(5L, "하연", null),
        )
    ),
    buttonTapCounts = listOf(
        TeamInsightButtonCount(1L, "기획서 업데이트", 30, "doc", "#FFC94C", 10L, "업무"),
        TeamInsightButtonCount(2L, "프론트 코드 수정", 19, "code", "#4C8DFF", 10L, "업무"),
        TeamInsightButtonCount(3L, "피그마 업데이트", 10, "figma", "#FF6B6B", 20L, "디자인"),
        TeamInsightButtonCount(4L, "톡방에 연락", 8, "chat", "#7C5CFF", 30L, "소통"),
        TeamInsightButtonCount(5L, "계획서 수정", 1, "edit", "#FFD54C", 10L, "업무"),
    ),
    memberActivity = listOf(
        TeamInsightMemberActivity(
            userId = 4L, displayName = "유하연", profileImageUrl = null, tapCount = 28,
            topButton = TeamInsightMemberTopButton(1L, "기획서 업데이트", 15, "doc", "#FFC94C")
        ),
        TeamInsightMemberActivity(
            userId = 7L, displayName = "정수민", profileImageUrl = null, tapCount = 22,
            topButton = TeamInsightMemberTopButton(2L, "프론트 코드 수정", 12, "code", "#4C8DFF")
        ),
        TeamInsightMemberActivity(
            userId = 9L, displayName = "김민지", profileImageUrl = null, tapCount = 18,
            topButton = TeamInsightMemberTopButton(3L, "피그마 업데이트", 8, "figma", "#FF6B6B")
        ),
    ),
    timeline = listOf(
        TeamInsightTimelineItem(
            recordId = 101L, teamButtonId = 1L, buttonName = "기획서 업데이트",
            iconName = "doc", iconColor = "#FFC94C",
            userId = 2L, displayName = "누리", profileImageUrl = null,
            recordedAt = LocalDateTime.now().minusHours(3).toString()
        ),
        TeamInsightTimelineItem(
            recordId = 102L, teamButtonId = 2L, buttonName = "프론트 코드 수정",
            iconName = "code", iconColor = "#4C8DFF",
            userId = 4L, displayName = "하연", profileImageUrl = null,
            recordedAt = LocalDateTime.now().minusHours(5).toString()
        ),
        TeamInsightTimelineItem(
            recordId = 103L, teamButtonId = 3L, buttonName = "피그마 업데이트",
            iconName = "figma", iconColor = "#FF6B6B",
            userId = 3L, displayName = "희경", profileImageUrl = null,
            recordedAt = LocalDateTime.now().minusHours(9).toString()
        ),
    )

)