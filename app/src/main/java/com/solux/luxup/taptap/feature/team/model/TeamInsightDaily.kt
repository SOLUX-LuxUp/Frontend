package com.solux.luxup.taptap.feature.team.model

/**
 * 팀 데일리 인사이트 (기능명세서 8.1.8)
 * GET /api/teams/{team_id}/insights/daily
 *
 * ⚠ 실제 응답 대비 백엔드 요청 대기 필드:
 *   - topButton.tappedMembers: 누가 눌렀는지(배너 아바타 오버랩용) — 응답 없음
 *   - buttonTapCounts[]: iconName / iconColor / categoryId / categoryName — 응답 없음
 *   - memberActivity[].topButton: iconName / iconColor — 응답 없음
 */
data class TeamInsightDaily(
    val teamId: Long,
    val targetDate: String,                               // 응답: targetDate "yyyy-MM-dd"
    val totalTapCount: Int,
    val topButton: TeamInsightTopButton?,                 // ② 배너
    val buttonTapCounts: List<TeamInsightButtonCount>,    // ③ 기록 비율
    val memberActivity: List<TeamInsightMemberActivity>,   // ④ 도넛 + ⑤ 최다 버튼
    val timeline: List<TeamInsightTimelineItem> = emptyList()  // ⚠ 요청 대기
)

/** ② 배너 — 오늘 팀 최다 버튼
 *  응답: teamButtonId/buttonName/iconName/iconColor/tapCount
 *  tappedMembers = ⚠ 백엔드 요청 대기 (아바타 오버랩용) */
data class TeamInsightTopButton(
    val teamButtonId: Long,
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
    val tapCount: Int,
    val tappedMembers: List<TeamInsightTappedMember> = emptyList()  // ⚠ 요청 대기
)

/** 배너 아바타 오버랩용 멤버 (요청 대기 필드) */
data class TeamInsightTappedMember(
    val userId: Long,
    val displayName: String,
    val profileImageUrl: String?   // ⚠ 프로필 사진 의존성 대기 → 지금은 기본 아바타
)

/** ③ 버튼 축 — 카테고리 필터 후 버튼 기록 비율(프로그레스)
 *  응답: teamButtonId/buttonName/tapCount
 *  iconName·iconColor·categoryId·categoryName = ⚠ 백엔드 요청 대기 */
data class TeamInsightButtonCount(
    val teamButtonId: Long,
    val buttonName: String,
    val tapCount: Int,
    val iconName: String? = null,       // ⚠ 요청 대기
    val iconColor: String? = null,      // ⚠ 요청 대기 (null이면 아이콘 원 회색)
    val categoryId: Long? = null,       // ⚠ 요청 대기
    val categoryName: String? = null    // ⚠ 요청 대기 (null이면 카테고리 필터 홀드)
)

/** ④⑤ 팀원 축 — 도넛 조각(tapCount) + 최다 버튼(topButton) */
data class TeamInsightMemberActivity(
    val userId: Long,
    val displayName: String,
    val profileImageUrl: String?,               // 응답에 있음 (지금은 이니셜 대체)
    val tapCount: Int,
    val topButton: TeamInsightMemberTopButton?
)

/** ⑤ 팀원별 최다 버튼
 *  응답: teamButtonId/buttonName/tapCount. iconName·iconColor = ⚠ 요청 대기 */
data class TeamInsightMemberTopButton(
    val teamButtonId: Long,
    val buttonName: String,
    val tapCount: Int,
    val iconName: String? = null,   // ⚠ 요청 대기
    val iconColor: String? = null   // ⚠ 요청 대기
)

/** 타임라인 항목 — ⚠ 8.1.8 응답에 없음, 백엔드 요청 대기
 *  recordedAt(ISO) 하나만 받고 "10:43 AM" / "3시간 전"은 FE 계산 */
data class TeamInsightTimelineItem(
    val recordId: Long,
    val teamButtonId: Long,
    val buttonName: String,
    val iconName: String? = null,    // ⚠ 요청 대기
    val iconColor: String? = null,   // ⚠ 요청 대기
    val userId: Long,
    val displayName: String,
    val profileImageUrl: String?,
    val recordedAt: String           // ISO-8601 "2026-05-03T10:43:00"
)