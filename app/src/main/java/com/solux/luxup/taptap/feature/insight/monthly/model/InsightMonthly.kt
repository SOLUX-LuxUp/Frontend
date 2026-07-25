package com.solux.luxup.taptap.feature.insight.monthly.model

import com.solux.luxup.taptap.feature.insight.daily.model.InsightButtonTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount

/**
 * 개인 먼슬리 인사이트 ("레포트" 탭 - Monthly)
 * GET /api/insights/monthly?year=YYYY&month=M
 *
 * categoryTapCounts/buttonTapCounts는 daily와 같은 타입 재사용.
 */
data class InsightMonthly(
    val year: Int,
    val month: Int,                                                      // 1~12
    val totalTapCount: Int,
    val dailyTapCounts: Map<String, Int> = emptyMap(),                   // 캘린더 히트맵 - key: "yyyy-MM-dd"
    val categoryTapCounts: List<InsightCategoryTapCount> = emptyList(),  // 카테고리 비율 도넛
    val buttonTapCounts: List<InsightButtonTapCount> = emptyList(),      // ⚠ 현재 화면 시안엔 없어 미사용 (추후 전체보기 등에서 사용 가능)
    val top3Buttons: List<InsightMonthlyRankedButton> = emptyList(),     // 버튼 TOP 3
    val topCategory: InsightMonthlyTopCategory? = null,                  // 가장 많이 기록한 카테고리
    val busiestDay: String? = null,                                      // 가장 많이 기록한 날 - "yyyy-MM-dd"
    val weekdayRatio: Double = 0.0,                                      // 평일 비율
    val weekendRatio: Double = 0.0,                                      // 주말 비율
    val timeSlotCategory: Map<String, InsightTimeSlotStat> = emptyMap(), // 시간대별 활동 분석 - key: "새벽"/"아침"/"점심"/"저녁"/"밤"
    val prevMonthComparison: InsightMonthlyComparison? = null            // 지난달 대비 기록 변화
)

/** 버튼 TOP 3 한 줄 — 순위 + 버튼 + 기록 수 */
data class InsightMonthlyRankedButton(
    val rank: Int,
    val buttonId: Long,
    val buttonName: String,
    val count: Int
)

/** 가장 많이 기록한 카테고리 */
data class InsightMonthlyTopCategory(
    val categoryId: Long,
    val categoryName: String,
    val count: Int
)

/** 시간대 한 칸 — 그 시간대에 가장 많이 기록된 카테고리 + 비중 */
data class InsightTimeSlotStat(
    val count: Int,
    val ratio: Double,
    val categoryId: Long?,
    val categoryName: String?
)

/** 지난달 대비 기록 변화 — 기록 횟수 증감 + TOP5 버튼 비교 */
data class InsightMonthlyComparison(
    val prevTotalTapCount: Int,
    val changeRate: Double,                                        // 증감률, 예: -0.17 = -17%
    val prevTop5Buttons: List<InsightMonthlyRankedButton> = emptyList(),
    val currentTop5Buttons: List<InsightMonthlyRankedButton> = emptyList()
)