package com.solux.luxup.taptap.feature.insight.daily.model

/**
 * 개인 데일리 인사이트 ("레포트" 탭 - Daily)
 * GET /api/insights/daily?date=yyyy-MM-dd
 */
data class InsightDaily(
    val targetDate: String,                                     // "yyyy-MM-dd"
    val totalTapCount: Int,
    val topButton: InsightTopButton?,                           // 요약 카드 - 가장 많은 기록
    val peakTimeSlot: String?,                                  // 요약 카드 - 자주 기록한 시간대 ("아침")
    val peakTimeSlotSimple: String?,                            // 요약 라벨 ("오전") - 주간/월간 등 압축 표기용
    val categoryTapCounts: List<InsightCategoryTapCount> = emptyList(), // 기록비율 전체보기 - 카테고리 도넛
    val buttonTapCounts: List<InsightButtonTapCount> = emptyList(),     // 기록비율 - 버튼별 목록
    val timeline: List<InsightTimelineItem> = emptyList()                // 타임라인
)

/** 오늘 가장 많이 누른 버튼 — 요약 카드 좌측 */
data class InsightTopButton(
    val buttonId: Long,
    val buttonName: String,
    val iconName: String?,
    val iconColor: String?,
    val count: Int
)

/** 카테고리별 집계 — 기록비율 전체보기 도넛 차트 */
data class InsightCategoryTapCount(
    val categoryId: Long,
    val categoryName: String,
    val count: Int,
    val ratio: Double,
    // ⚠ API 미제공 — 도넛 조각 색. null이면 순번 기준 고정 팔레트로 대체
    val categoryColor: String? = null
)

/** 버튼별 집계 — 기록비율 목록 (ALL▾ 은 categoryId/categoryName 기준으로 필터링) */
data class InsightButtonTapCount(
    val buttonId: Long,
    val buttonName: String,
    val count: Int,
    val ratio: Double,
    // ⚠ API 미제공 — 값이 오면 실제 아이콘, 없으면 이니셜로 폴백
    val iconName: String? = null,
    val iconColor: String? = null,
    // ⚠ API 미제공 — ALL▾ 카테고리 필터에 사용. null이면 필터링 시 제외되지 않고 "ALL"에서만 노출
    val categoryId: Long? = null,
    val categoryName: String? = null
)

/** 타임라인 항목 */
data class InsightTimelineItem(
    val recordId: Long,
    val buttonId: Long,
    val buttonName: String,
    val recordedAt: String,           // ISO-8601 (UTC) — 시각·경과시간은 FE 계산
    val memo: String? = null,
    val emoji: String? = null,
    // ⚠ API 미제공 — 값이 오면 실제 아이콘, 없으면 이니셜로 폴백
    val iconName: String? = null,
    val iconColor: String? = null
)
