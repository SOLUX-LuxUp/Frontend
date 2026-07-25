package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.TeamInsightBarCategory
import com.solux.luxup.taptap.feature.team.model.TeamInsightButtonCount
import com.solux.luxup.taptap.feature.team.model.TeamInsightCalendarDay
import com.solux.luxup.taptap.feature.team.model.TeamInsightCategory
import com.solux.luxup.taptap.feature.team.model.TeamInsightCategoryCount
import com.solux.luxup.taptap.feature.team.model.TeamInsightDaily
import com.solux.luxup.taptap.feature.team.model.TeamInsightDailyBar
import com.solux.luxup.taptap.feature.team.model.TeamInsightMember
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberActivity
import com.solux.luxup.taptap.feature.team.model.TeamInsightMemberTopButton
import com.solux.luxup.taptap.feature.team.model.TeamInsightMonthly
import com.solux.luxup.taptap.feature.team.model.TeamInsightTimelineItem
import com.solux.luxup.taptap.feature.team.model.TeamInsightTopButton
import com.solux.luxup.taptap.feature.team.model.TeamInsightWeekly
import java.time.LocalDateTime

// 공용 멤버 목데이터
private val MockMemberNuri = TeamInsightMember(2L, "누리", null)
private val MockMemberHayeon = TeamInsightMember(3L, "하연", null)
private val MockMemberHeegyeong = TeamInsightMember(4L, "희경", null)
private val MockMemberSumin = TeamInsightMember(5L, "수민", null)

/* ---------------------------------- DAILY ---------------------------------- */

/** 데일리 목데이터 — 8.1.8 daily 응답 형태 기준 */
val MockTeamInsightDaily = TeamInsightDaily(
    teamId = 1L,
    targetDate = "2026-05-03",
    totalTapCount = 68,
    topButton = TeamInsightTopButton(
        teamButtonId = 5L,
        buttonName = "출석",
        iconName = "check-circle",
        iconColor = "#FFC107",
        tapCount = 7,
        tappedMembers = listOf(
            MockMemberNuri, MockMemberHayeon, MockMemberHeegyeong, MockMemberSumin
        )
    ),
    timeline = listOf(
        TeamInsightTimelineItem(
            teamButtonId = 6L,
            buttonName = "기획서 업데이트",
            iconName = "document",
            iconColor = "#FFC107",
            tappedAt = LocalDateTime.now().minusHours(3).toString(),
            member = MockMemberNuri
        ),
        TeamInsightTimelineItem(
            teamButtonId = 1L,
            buttonName = "프론트 코드 수정",
            iconName = "code",
            iconColor = "#4C8DFF",
            tappedAt = LocalDateTime.now().minusHours(5).toString(),
            member = MockMemberHayeon
        ),
        TeamInsightTimelineItem(
            teamButtonId = 7L,
            buttonName = "피그마 업데이트",
            iconName = "pencil",
            iconColor = "#FF5C5C",
            tappedAt = LocalDateTime.now().minusHours(9).toString(),
            member = MockMemberHeegyeong
        ),
    ),
    categories = listOf(
        TeamInsightCategory(10L, "건강", "#4C8DFF", 8),
        TeamInsightCategory(20L, "자기계발", "#FFB84C", 60),
    ),
    buttonTapCounts = listOf(
        TeamInsightButtonCount(6L, "기획서 업데이트", "document", "#FFC107", 20L, "자기계발", 30),
        TeamInsightButtonCount(1L, "프론트 코드 수정", "code", "#4C8DFF", 20L, "자기계발", 19),
        TeamInsightButtonCount(7L, "피그마 업데이트", "pencil", "#FF5C5C", 20L, "자기계발", 10),
        TeamInsightButtonCount(8L, "톡방에 연락", "person", "#4C8DFF", 10L, "건강", 8),
        TeamInsightButtonCount(9L, "계획서 수정", "edit", "#FFC107", 20L, "자기계발", 1),
    ),
    memberActivity = listOf(
        TeamInsightMemberActivity(
            userId = 2L, displayName = "누리", profileImageUrl = null, tapCount = 28,
            topButton = TeamInsightMemberTopButton(6L, "기획서 업데이트", "document", "#FFC107", 15)
        ),
        TeamInsightMemberActivity(
            userId = 5L, displayName = "수민", profileImageUrl = null, tapCount = 22,
            topButton = TeamInsightMemberTopButton(1L, "프론트 코드 수정", "code", "#4C8DFF", 12)
        ),
        TeamInsightMemberActivity(
            userId = 4L, displayName = "희경", profileImageUrl = null, tapCount = 18,
            topButton = TeamInsightMemberTopButton(7L, "피그마 업데이트", "pencil", "#FF5C5C", 8)
        ),
    )
)

/* ---------------------------------- WEEKLY --------------------------------- */

/** 위클리 목데이터 — 요일 합 = totalTapCount(56), 멤버 합도 56 */
val MockTeamInsightWeekly = TeamInsightWeekly(
    teamId = 1L,
    weekStart = "2026-05-04",
    weekEnd = "2026-05-10",
    totalTapCount = 56,
    topButton = TeamInsightTopButton(
        teamButtonId = 5L,
        buttonName = "출석",
        iconName = "check-circle",
        iconColor = "#FFC107",
        tapCount = 38,
        tappedMembers = listOf(
            MockMemberNuri, MockMemberHayeon, MockMemberHeegyeong, MockMemberSumin
        )
    ),
    dailyTapCounts = listOf(
        TeamInsightDailyBar("2026-05-04", 7, listOf(
            TeamInsightBarCategory(10L, "건강", "#4C8DFF", 4),
            TeamInsightBarCategory(20L, "자기계발", "#FFB84C", 3),
        )),
        TeamInsightDailyBar("2026-05-05", 11, listOf(
            TeamInsightBarCategory(10L, "건강", "#4C8DFF", 7),
            TeamInsightBarCategory(20L, "자기계발", "#FFB84C", 4),
        )),
        TeamInsightDailyBar("2026-05-06", 6, listOf(
            TeamInsightBarCategory(10L, "건강", "#4C8DFF", 2),
            TeamInsightBarCategory(20L, "자기계발", "#FFB84C", 4),
        )),
        TeamInsightDailyBar("2026-05-07", 8, listOf(
            TeamInsightBarCategory(10L, "건강", "#4C8DFF", 5),
            TeamInsightBarCategory(20L, "자기계발", "#FFB84C", 3),
        )),
        TeamInsightDailyBar("2026-05-08", 12, listOf(
            TeamInsightBarCategory(10L, "건강", "#4C8DFF", 4),
            TeamInsightBarCategory(20L, "자기계발", "#FFB84C", 8),
        )),
        TeamInsightDailyBar("2026-05-09", 5, listOf(
            TeamInsightBarCategory(10L, "건강", "#4C8DFF", 2),
            TeamInsightBarCategory(20L, "자기계발", "#FFB84C", 3),
        )),
        TeamInsightDailyBar("2026-05-10", 7, listOf(
            TeamInsightBarCategory(10L, "건강", "#4C8DFF", 4),
            TeamInsightBarCategory(20L, "자기계발", "#FFB84C", 3),
        )),
    ),
    buttonTapCounts = listOf(
        TeamInsightButtonCount(6L, "기획서 업데이트", "document", "#FFC107", 20L, "자기계발", 30),
        TeamInsightButtonCount(1L, "프론트 코드 수정", "code", "#4C8DFF", 20L, "자기계발", 23),
        TeamInsightButtonCount(7L, "피그마 업데이트", "pencil", "#FF5C5C", 20L, "자기계발", 19),
        TeamInsightButtonCount(8L, "톡방에 연락", "person", "#4C8DFF", 10L, "건강", 10),
        TeamInsightButtonCount(9L, "계획서 수정", "edit", "#FFC107", 20L, "자기계발", 8),
    ),
    memberActivity = listOf(
        TeamInsightMemberActivity(
            userId = 2L, displayName = "누리", profileImageUrl = null, tapCount = 20,
            topButton = TeamInsightMemberTopButton(6L, "기획서 업데이트", "document", "#FFC107", 8)
        ),
        TeamInsightMemberActivity(
            userId = 4L, displayName = "희경", profileImageUrl = null, tapCount = 13,
            topButton = TeamInsightMemberTopButton(7L, "피그마 업데이트", "pencil", "#FF5C5C", 7)
        ),
        TeamInsightMemberActivity(
            userId = 5L, displayName = "수민", profileImageUrl = null, tapCount = 10,
            topButton = TeamInsightMemberTopButton(1L, "프론트 코드 수정", "code", "#4C8DFF", 13)
        ),
        TeamInsightMemberActivity(
            userId = 3L, displayName = "하연", profileImageUrl = null, tapCount = 13,
            topButton = TeamInsightMemberTopButton(9L, "계획서 수정", "edit", "#FFC107", 5)
        ),
    )
)

/* --------------------------------- MONTHLY --------------------------------- */

/** 먼슬리 목데이터 — 8.1.8 monthly 응답 명세 기준 (색상 이름 표기) */
val MockTeamInsightMonthly = TeamInsightMonthly(
    teamId = 1L,
    year = 2026,
    month = 5,
    totalTapCount = 40,
    topButton = TeamInsightTopButton(
        teamButtonId = 5L,
        buttonName = "출석",
        iconName = "check-circle",
        iconColor = "orange",              // 명세: 색 이름
        tapCount = 78,
        tappedMembers = listOf(
            MockMemberNuri, MockMemberHayeon, MockMemberHeegyeong, MockMemberSumin
        )
    ),
    dailyTapCounts = listOf(
        TeamInsightCalendarDay("2026-05-01", 5),    // LV1
        TeamInsightCalendarDay("2026-05-04", 12),   // LV2
        TeamInsightCalendarDay("2026-05-05", 25),   // LV3
        TeamInsightCalendarDay("2026-05-06", 33),   // LV4
        TeamInsightCalendarDay("2026-05-09", 45),   // LV5
        TeamInsightCalendarDay("2026-05-10", 42),   // LV5
        TeamInsightCalendarDay("2026-05-20", 22),   // LV3
        TeamInsightCalendarDay("2026-05-21", 35),   // LV4
        TeamInsightCalendarDay("2026-05-22", 48),   // LV5
        TeamInsightCalendarDay("2026-05-25", 15),   // LV2
        TeamInsightCalendarDay("2026-05-31", 8),    // LV1
    ),
    categoryTapCounts = listOf(  // ⚠ 현재 화면 미사용
        TeamInsightCategoryCount(10L, "건강", "blue", 20, 0.51),      // 명세: 색 이름
        TeamInsightCategoryCount(20L, "자기계발", "orange", 20, 0.49),
    ),
    memberActivity = listOf(
        TeamInsightMemberActivity(
            userId = 2L, displayName = "누리", profileImageUrl = null, tapCount = 10,
            topButton = TeamInsightMemberTopButton(6L, "기획서 업데이트", "document", "orange", 1)
        ),
        TeamInsightMemberActivity(
            userId = 4L, displayName = "희경", profileImageUrl = null, tapCount = 4,
            topButton = TeamInsightMemberTopButton(7L, "피그마 업데이트", "pencil", "red", 4)
        ),
        TeamInsightMemberActivity(
            userId = 5L, displayName = "수민", profileImageUrl = null, tapCount = 5,
            topButton = TeamInsightMemberTopButton(1L, "프론트 코드 수정", "code", "blue", 5)
        ),
        TeamInsightMemberActivity(
            userId = 3L, displayName = "하연", profileImageUrl = null, tapCount = 2,
            topButton = TeamInsightMemberTopButton(5L, "출석", "check-circle", "orange", 5)
        ),
    )
)