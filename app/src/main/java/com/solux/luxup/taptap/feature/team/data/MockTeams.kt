package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.LatestRecord
import com.solux.luxup.taptap.feature.team.model.MemberProfile
import com.solux.luxup.taptap.feature.team.model.Team

private fun mockMembers(n: Int): List<MemberProfile> =
    (1..n).map { MemberProfile(it.toLong(), "멤버$it", null) }

val mockTeams = listOf(
    Team(
        teamId = 1,
        teamName = "LUX-UP",
        teamImageUrl = null,
        isFavorite = true,
        maxMember = 30,
        memberCount = 6,
        memberProfiles = mockMembers(6),
        latestRecord = LatestRecord(3, "기획서 업로드", "exercise", "2025-05-23T14:33:00"),
        recentUpdatedMembers = mockMembers(3),           // 업데이트 바용
        updatedAt = "2025-05-23T14:33:00"
    ),
    Team(
        teamId = 2,
        teamName = "SOLUX",
        teamImageUrl = null,
        isFavorite = false,
        maxMember = 20,
        memberCount = 4,
        memberProfiles = mockMembers(4),
        latestRecord = LatestRecord(5, "공지 업로드", "study", "2025-05-23T14:30:00"),
        recentUpdatedMembers = mockMembers(2),
        updatedAt = "2025-05-23T14:30:00"
    ),
    Team(
        teamId = 3,
        teamName = "눈송이들",
        teamImageUrl = null,
        isFavorite = true,
        maxMember = 15,
        memberCount = 2,
        memberProfiles = mockMembers(2),
        latestRecord = LatestRecord(7, "밥먹기", "meal", "2025-05-23T13:57:00"),
        recentUpdatedMembers = mockMembers(1),
        updatedAt = "2025-05-23T13:57:00"
    )
)