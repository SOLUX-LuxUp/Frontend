package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.RecentRecord
import com.solux.luxup.taptap.feature.team.model.Team

val mockTeams = listOf(
    Team(1, "LUX - UP", isFavorite = true, memberCount = 5, recentRecord = RecentRecord("공지 업로드", "3분 전"), updatedMemberCount = 4),
    Team(2, "SOLUX", isFavorite = false, memberCount = 12, recentRecord = RecentRecord("자료 확인", "1시간 전"), updatedMemberCount = 3),
    Team(3, "눈송이들", isFavorite = false, memberCount = 9, recentRecord = RecentRecord("밥먹기", "어제"), updatedMemberCount = 2)
)