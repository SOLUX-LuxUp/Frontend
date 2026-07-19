package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.TeamInsightButtonCount
import com.solux.luxup.taptap.feature.team.model.TeamInsightDaily
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberActivity
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberTopButton
import com.solux.luxup.taptap.feature.team.model.TeamInsightTappedMember
import com.solux.luxup.taptap.feature.team.model.TeamInsightTopButton
import com.solux.luxup.taptap.feature.team.model.TeamInsightTimelineItem
import java.time.LocalDateTime
import com.solux.luxup.taptap.feature.team.model.TeamInsightBarCategory
import com.solux.luxup.taptap.feature.team.model.TeamInsightDailyBar
import com.solux.luxup.taptap.feature.team.model.TeamInsightWeekly
import com.solux.luxup.taptap.feature.team.model.TeamInsightCalendarDay
import com.solux.luxup.taptap.feature.team.model.TeamInsightCategoryCount
import com.solux.luxup.taptap.feature.team.model.TeamInsightMonthly
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
/**
 * 위클리 목데이터 — 요청 반영 후 형태 미리 가정(categories 객체 배열 + categoryColor) 기준
 * 요일 합 = totalTapCount 되도록 구성
 */
val MockTeamInsightWeekly = TeamInsightWeekly(
    teamId = 1L,
    weekStart = "2026-05-04",
    weekEnd = "2026-05-10",
    totalTapCount = 56,
    topButton = TeamInsightTopButton(
        teamButtonId = 1L,
        buttonName = "출석",
        iconName = "doc",
        iconColor = "#FFC94C",
        tapCount = 38,
        tappedMembers = listOf(
            TeamInsightTappedMember(4L, "누리", null),
            TeamInsightTappedMember(7L, "수민", null),
            TeamInsightTappedMember(9L, "정민", null),
            TeamInsightTappedMember(2L, "은서", null),
        )
    ),
    dailyTapCounts = listOf(
        TeamInsightDailyBar("2026-05-04", 7, listOf(
            TeamInsightBarCategory(10L, "업무", "#FFC94C", 4),
            TeamInsightBarCategory(20L, "디자인", "#4C8DFF", 3),
        )),
        TeamInsightDailyBar("2026-05-05", 11, listOf(
            TeamInsightBarCategory(10L, "업무", "#FFC94C", 7),
            TeamInsightBarCategory(20L, "디자인", "#4C8DFF", 4),
        )),
        TeamInsightDailyBar("2026-05-06", 6, listOf(
            TeamInsightBarCategory(20L, "디자인", "#4C8DFF", 4),
            TeamInsightBarCategory(30L, "소통", "#FF6B6B", 2),
        )),
        TeamInsightDailyBar("2026-05-07", 8, listOf(
            TeamInsightBarCategory(10L, "업무", "#FFC94C", 5),
            TeamInsightBarCategory(30L, "소통", "#FF6B6B", 3),
        )),
        TeamInsightDailyBar("2026-05-08", 12, listOf(
            TeamInsightBarCategory(30L, "소통", "#FF6B6B", 8),
            TeamInsightBarCategory(10L, "업무", "#FFC94C", 4),
        )),
        TeamInsightDailyBar("2026-05-09", 5, listOf(
            TeamInsightBarCategory(20L, "디자인", "#4C8DFF", 3),
            TeamInsightBarCategory(10L, "업무", "#FFC94C", 2),
        )),
        TeamInsightDailyBar("2026-05-10", 7, listOf(
            TeamInsightBarCategory(10L, "업무", "#FFC94C", 4),
            TeamInsightBarCategory(20L, "디자인", "#4C8DFF", 3),
        )),
    ),
    buttonTapCounts = listOf(
        TeamInsightButtonCount(1L, "기획서 업데이트", 30, "doc", "#FFC94C", 10L, "업무"),
        TeamInsightButtonCount(2L, "프론트 코드 수정", 23, "code", "#4C8DFF", 10L, "업무"),
        TeamInsightButtonCount(3L, "피그마 업데이트", 19, "figma", "#FF6B6B", 20L, "디자인"),
        TeamInsightButtonCount(4L, "톡방에 연락", 10, "chat", "#7C5CFF", 30L, "소통"),
        TeamInsightButtonCount(5L, "계획서 수정", 8, "edit", "#FFD54C", 10L, "업무"),
    ),
    memberActivity = listOf(
        TeamInsightMemberActivity(
            userId = 4L, displayName = "누리", profileImageUrl = null, tapCount = 20,
            topButton = TeamInsightMemberTopButton(1L, "기획서 업데이트", 8, "doc", "#FFC94C")
        ),
        TeamInsightMemberActivity(
            userId = 3L, displayName = "희경", profileImageUrl = null, tapCount = 13,
            topButton = TeamInsightMemberTopButton(3L, "피그마 업데이트", 7, "figma", "#FF6B6B")
        ),
        TeamInsightMemberActivity(
            userId = 7L, displayName = "수민", profileImageUrl = null, tapCount = 10,
            topButton = TeamInsightMemberTopButton(2L, "프론트 코드 수정", 13, "code", "#4C8DFF")
        ),
        TeamInsightMemberActivity(
            userId = 2L, displayName = "은서", profileImageUrl = null, tapCount = 13,
            topButton = TeamInsightMemberTopButton(5L, "홈싸비 추정", 5, "edit", "#FFD54C")
        ),
    )
)
/**
 * 먼슬리 목데이터 — 시안 기준 (2026년 5월, 총 40회, 배너 출석 78회)
 * ⚠ 시안 히트맵에 색칠된 날짜만 tapCount > 0, 나머지는 응답에 없거나 0
 */
val MockTeamInsightMonthly = TeamInsightMonthly(
    teamId = 1L,
    year = 2026,
    month = 5,
    totalTapCount = 40,
    topButton = TeamInsightTopButton(
        teamButtonId = 1L,
        buttonName = "출석",
        iconName = "doc",
        iconColor = "#FFC94C",
        tapCount = 78,
        tappedMembers = listOf(
            TeamInsightTappedMember(4L, "누리", null),
            TeamInsightTappedMember(7L, "수민", null),
            TeamInsightTappedMember(9L, "정민", null),
            TeamInsightTappedMember(2L, "은서", null),
        )
    ),
    dailyTapCounts = listOf(
        TeamInsightCalendarDay("2026-05-01", 5),    // 금 — LV1
        TeamInsightCalendarDay("2026-05-04", 12),   // 월 — LV2
        TeamInsightCalendarDay("2026-05-05", 25),   // 화 — LV3
        TeamInsightCalendarDay("2026-05-06", 33),   // 수 — LV4
        TeamInsightCalendarDay("2026-05-09", 45),   // 토 — LV5
        TeamInsightCalendarDay("2026-05-10", 42),   // 일 — LV5
        TeamInsightCalendarDay("2026-05-20", 22),   // 수 — LV3
        TeamInsightCalendarDay("2026-05-21", 35),   // 목 — LV4
        TeamInsightCalendarDay("2026-05-22", 48),   // 금 — LV5
        TeamInsightCalendarDay("2026-05-25", 15),   // 월 — LV2
        TeamInsightCalendarDay("2026-05-31", 8),    // 일 — LV1
    ),
    categoryTapCounts = listOf(  // ⚠ 현재 화면 미사용
        TeamInsightCategoryCount(10L, "업무", 500, 0.51),
        TeamInsightCategoryCount(20L, "디자인", 480, 0.49),
    ),
    buttonTapCounts = listOf(    // ⚠ 현재 화면 미사용
        TeamInsightButtonCount(1L, "기획서 업데이트", 320, "doc", "#FFC94C", 10L, "업무"),
        TeamInsightButtonCount(2L, "프론트 코드 수정", 280, "code", "#4C8DFF", 10L, "업무"),
    ),
    memberActivity = listOf(
        TeamInsightMemberActivity(
            userId = 4L, displayName = "누리", profileImageUrl = null, tapCount = 10,
            topButton = TeamInsightMemberTopButton(1L, "기획서 업데이트", 1, "doc", "#FFC94C")
        ),
        TeamInsightMemberActivity(
            userId = 3L, displayName = "희경", profileImageUrl = null, tapCount = 4,
            topButton = TeamInsightMemberTopButton(3L, "피그마 업데이트", 4, "figma", "#FF6B6B")
        ),
        TeamInsightMemberActivity(
            userId = 7L, displayName = "수민", profileImageUrl = null, tapCount = 5,
            topButton = TeamInsightMemberTopButton(2L, "프론트 코드 수정", 5, "code", "#4C8DFF")
        ),
        TeamInsightMemberActivity(
            userId = 2L, displayName = "은서", profileImageUrl = null, tapCount = 2,
            topButton = TeamInsightMemberTopButton(5L, "출석", 5, "doc", "#FFC94C")
        ),
    )
)