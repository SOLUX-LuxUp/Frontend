package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.*

val mockTeamButtons = TeamButtonsResponse(
    favoriteButtons = listOf(
        TeamButton(
            teamButtonId = 1, buttonName = "기획서 업데이트",
            iconName = "book", iconColor = "#FFC107",
            isFavorite = true, tapPermission = "all",
            categoryId = 1, categoryName = "PROJECT", hasTapPermission = true,
            latestRecord = ButtonRecord(
                "2025-05-23T11:41:00",
                MemberProfile(2, "누리", null)
            )
        )
    ),
    buttons = listOf(
        TeamButton(
            teamButtonId = 2, buttonName = "프론트 코드 수정",
            iconName = "laptop", iconColor = "#3357FF",
            isFavorite = false, tapPermission = "all",
            categoryId = 1, categoryName = "PROJECT", hasTapPermission = true,
            latestRecord = ButtonRecord(
                "2025-05-23T04:58:00",
                MemberProfile(3, "멤버3", null)
            )
        ),
        TeamButton(
            teamButtonId = 3, buttonName = "독서",
            iconName = "book", iconColor = "#33C758",
            isFavorite = false, tapPermission = "all",
            categoryId = null, categoryName = null, hasTapPermission = true,
            latestRecord = null              // 기록 없는 경우도 하나
        )
    )
)