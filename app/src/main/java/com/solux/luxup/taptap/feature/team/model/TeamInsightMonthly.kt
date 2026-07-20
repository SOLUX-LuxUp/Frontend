package com.solux.luxup.taptap.feature.team.model

/**
 * 팀 먼슬리 인사이트 (기능명세서 8.1.8)
 * GET /api/teams/{team_id}/insights/monthly?year=YYYY&month=M
 *
 * 공통 필드(topButton/memberActivity)는 daily·weekly와 같은 타입 재사용.
 * monthly 전용: dailyTapCounts (캘린더 히트맵)
 *
 * ⚠ 응답엔 있으나 시안에 없어 현재 화면에서 미사용:
 *   - categoryTapCounts, buttonTapCounts (monthly 시안엔 "기록 비율" 섹션 없음)
 *     → 모델엔 두되 화면엔 안 그림. 시안 추가되면 바로 연결 가능.
 *
 * ⚠ 백엔드 요청 대기 (daily·weekly와 동일):
 *   - topButton.tappedMembers: 배너 아바타용
 *   - buttonTapCounts[]: iconName / iconColor / categoryId / categoryName
 *   - memberActivity[].topButton: iconName / iconColor
 */
data class TeamInsightMonthly(
    val teamId: Long,
    val year: Int,
    val month: Int,                                       // 1~12
    val totalTapCount: Int,
    val topButton: TeamInsightTopButton?,                 // ③ 배너 (동일 타입)
    val dailyTapCounts: List<TeamInsightCalendarDay>,     // ② 캘린더 히트맵 (monthly 전용)
    val categoryTapCounts: List<TeamInsightCategoryCount> = emptyList(), // ⚠ 현재 미사용
    val memberActivity: List<TeamInsightMemberActivity>   // ④⑤ 활동량·최다버튼 (동일 타입)
)

/** ② 캘린더 히트맵 한 칸 — 그날 기록 수 */
data class TeamInsightCalendarDay(
    val date: String,    // "2025-05-01"
    val tapCount: Int
)

/** 카테고리별 집계 — ⚠ 응답엔 오지만 monthly 시안엔 해당 섹션 없어서 현재 미사용중 */
data class TeamInsightCategoryCount(
    val categoryId: Long,
    val categoryName: String,
    val categoryColor: String,
    val tapCount: Int,
    val ratio: Double
)


/** 히트맵 색 단계 — 시안 범례 고정 임계값 */
enum class HeatLevel(val label: String) {
    NONE("기록 없음"),
    LV1("10회 미만"),
    LV2("10회 이상\n20회 미만"),   // 줄바꿈 명시
    LV3("20회 이상\n30회 미만"),
    LV4("30회 이상\n40회 미만"),
    LV5("40회 이상");

    companion object {
        fun of(tapCount: Int): HeatLevel = when {
            tapCount <= 0 -> NONE
            tapCount < 10 -> LV1
            tapCount < 20 -> LV2
            tapCount < 30 -> LV3
            tapCount < 40 -> LV4
            else -> LV5
        }
    }
}