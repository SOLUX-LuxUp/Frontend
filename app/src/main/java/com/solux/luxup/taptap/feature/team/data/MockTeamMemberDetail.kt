package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.TeamMemberButton
import com.solux.luxup.taptap.feature.team.model.TeamMemberDetail
import com.solux.luxup.taptap.feature.team.model.TeamMemberRecord

val MockTeamMemberDetail = TeamMemberDetail(
    userId = 3,
    displayName = "홍길동",
    profileImageUrl = null,             // null → ic_profile 플레이스홀더
    hasMore = false,
    nextCursor = null,
    buttons = listOf(
        TeamMemberButton(5, "독서", "book", "#3357FF"),
        TeamMemberButton(6, "코드 수정", "code", "#2085FF"),
        TeamMemberButton(7, "필기하기", "pen", "#FF8A3D"),
    ),
    recentTimeline = listOf(
        TeamMemberRecord(20, "독서", "2025-05-23T11:00:00", "오늘 30페이지", "📖"),
        TeamMemberRecord(19, "독서", "2025-05-22T20:00:00", null, null),
        TeamMemberRecord(18, "코드 수정", "2025-05-22T14:30:00", "버그 수정 완료", "💻"),
        TeamMemberRecord(17, "필기하기", "2025-05-21T09:15:00", null, "✏️"),
    ),
)