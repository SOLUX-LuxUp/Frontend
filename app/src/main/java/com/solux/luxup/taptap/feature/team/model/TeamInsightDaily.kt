package com.solux.luxup.taptap.feature.team.model

/**
 * 팀 데일리 인사이트 (기능명세서 8.1.8)
 * GET /api/teams/{team_id}/insights/daily
 * 백엔드 응답 수정 반영 완료
 */
data class TeamInsightDaily(
    val teamId: Long,
    val targetDate: String,                                     // "yyyy-MM-dd"
    val totalTapCount: Int,
    val topButton: TeamInsightTopButton?,                       // ② 배너
    val timeline: List<TeamInsightTimelineItem> = emptyList(),  // ③ 타임라인
    val categories: List<TeamInsightCategory> = emptyList(),    // 카테고리 집계 (daily 전용)
    val buttonTapCounts: List<TeamInsightButtonCount>,          // ④ 기록 비율
    val memberActivity: List<TeamInsightMemberActivity>         // ⑤⑥ 활동량·최다버튼
)

/** ② 배너 — 오늘 팀 최다 버튼 */
data class TeamInsightTopButton(
    val teamButtonId: Long,
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
    val tapCount: Int,
    val tappedMembers: List<TeamInsightMember> = emptyList()
)

/** 팀원 요약 — 배너 아바타 / 타임라인 기록자 공용 */
data class TeamInsightMember(
    val userId: Long,
    val displayName: String,
    val profileImageUrl: String?
)

/** ③ 타임라인 항목 — 누가 언제 무슨 버튼을 눌렀는지 */
data class TeamInsightTimelineItem(
    val teamButtonId: Long,
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
    val tappedAt: String,              // ISO-8601 — 시각·경과시간은 FE 계산
    val member: TeamInsightMember
)

/** 카테고리 집계 — daily 응답 전용 (weekly엔 없음) */
data class TeamInsightCategory(
    val categoryId: Long,
    val categoryName: String,
    val categoryColor: String,
    val tapCount: Int
)

/** ④ 버튼 축 — 기록 비율 (daily·weekly 공용 타입) */
data class TeamInsightButtonCount(
    val teamButtonId: Long,
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
    val categoryId: Long,
    val categoryName: String,
    val tapCount: Int
)

/** ⑤⑥ 팀원 축 — 도넛 조각 + 최다 버튼 (daily·weekly·monthly 공용 타입) */
data class TeamInsightMemberActivity(
    val userId: Long,
    val displayName: String,
    val profileImageUrl: String?,
    val tapCount: Int,
    val topButton: TeamInsightMemberTopButton?
)

/** ⑥ 팀원별 최다 버튼 */
data class TeamInsightMemberTopButton(
    val teamButtonId: Long,
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
    val tapCount: Int
)