package com.solux.luxup.taptap.feature.team.model

// 8.2.3 GET /api/teams/{team_id}/members/{user_id}/records 응답의 data
data class TeamMemberDetail(
    val userId: Long,
    val displayName: String,
    val profileImageUrl: String?,
    val hasMore: Boolean,               // 타임라인 페이징용
    val nextCursor: Long?,              // 다음 페이지 커서 (없으면 null)
    val buttons: List<TeamMemberButton>,    // 공유 중인 버튼 목록
    val recentTimeline: List<TeamMemberRecord>
)

data class TeamMemberButton(
    val buttonId: Long,
    val buttonName: String,
    val iconName: String,               // 예: "book" (아이콘 매핑용)
    val iconColor: String               // 예: "#3357FF"
)

data class TeamMemberRecord(
    val recordId: Long,
    val buttonName: String,
    val recordedAt: String,             // ISO-8601
    val memo: String?,                  // 없으면 null
    val emoji: String?                  // 없으면 null
)