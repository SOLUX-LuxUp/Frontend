package com.solux.luxup.taptap.feature.team.model

/**
 * 팀 위클리 인사이트 (기능명세서 8.1.8)
 * GET /api/teams/{team_id}/insights/weekly?week_start=YYYY-MM-DD
 *
 * daily와 공통 필드(topButton/buttonTapCounts/memberActivity)는 같은 타입 재사용.
 * weekly 전용: dailyTapCounts (요일별 카테고리 막대)
 *
 */
data class TeamInsightWeekly(
    val teamId: Long,
    val weekStart: String,                                // "2025-05-19"
    val weekEnd: String,                                  // "2025-05-25"
    val totalTapCount: Int,
    val topButton: TeamInsightTopButton?,                 // ② 배너 (daily와 동일 타입)
    val dailyTapCounts: List<TeamInsightDailyBar>,        // ③ 활동 기록 (weekly 전용)
    val buttonTapCounts: List<TeamInsightButtonCount>,    // ④ 기록 비율 (daily와 동일 타입)
    val memberActivity: List<TeamInsightMemberActivity>   // ⑤⑥ 활동량·최다버튼 (동일 타입)
)

/** ③ 요일별 막대 한 칸 — 하루치 기록 수 + 카테고리 구성 */
data class TeamInsightDailyBar(
    val date: String,                                     // "2025-05-19"
    val tapCount: Int,                                    // 그날 총 기록 수 (막대 높이)
    val categories: List<TeamInsightBarCategory>          // 스택 구성 (아래→위)
)

/** ③ 막대 안 카테고리 조각
 *   !! 현재 응답은 categoryRatio: { "health": 0.6 } 형태 (색·id 없음)
 *     → 객체 배열 + categoryColor로 변경 요청해둔 상태. 이 모델은 요청 반영 후 기준. */
data class TeamInsightBarCategory(
    val categoryId: Long,
    val categoryName: String,
    val categoryColor: String,   // "#RRGGBB" — 서버가 생성 순서대로 부여
    val tapCount: Int
)