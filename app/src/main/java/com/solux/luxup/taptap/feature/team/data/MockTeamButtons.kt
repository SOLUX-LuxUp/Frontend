package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.*

val mockTeamButtons = TeamButtonsResponse(
    buttons = listOf(
        TeamButton(
            teamButtonId = 1, buttonName = "기획서 업데이트",
            iconName = "book", iconColor = "#FFC107",
            tapPermission = "all",
            categoryId = 1, categoryName = "PROJECT", hasTapPermission = true,
            latestRecord = ButtonRecord(
                recordedAt = "2025-05-23T11:41:00",
                recordedBy = listOf(MemberProfile(2, "누리", null)),
                recordedByCount = 1
            )
        ),
        TeamButton(
            teamButtonId = 2, buttonName = "프론트 코드 수정",
            iconName = "laptop", iconColor = "#3357FF",
            tapPermission = "all",
            categoryId = 1, categoryName = "PROJECT", hasTapPermission = true,
            latestRecord = ButtonRecord(
                recordedAt = "2025-05-23T04:58:00",
                recordedBy = listOf(
                    MemberProfile(3, "멤버3", null),
                    MemberProfile(4, "멤버4", null)
                ),
                recordedByCount = 2
            )
        ),
        TeamButton(
            teamButtonId = 3, buttonName = "독서",
            iconName = "book", iconColor = "#33C758",
            tapPermission = "all",
            categoryId = null, categoryName = null, hasTapPermission = true,
            latestRecord = null              // 기록 없는 경우도 하나
        )
    )
)