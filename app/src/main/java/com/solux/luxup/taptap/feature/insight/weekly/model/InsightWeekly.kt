package com.solux.luxup.taptap.feature.insight.weekly.model

import com.solux.luxup.taptap.feature.insight.daily.model.InsightButtonTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightTopButton

/**
 * 개인 위클리 인사이트 ("레포트" 탭 - Weekly)
 * GET /api/insights/weekly?week_start=yyyy-MM-dd
 *
 * topButton/buttonTapCounts/categoryTapCounts는 daily와 같은 타입 재사용.
 */
data class InsightWeekly(
    val weekStart: String,                                              // "yyyy-MM-dd" (월요일)
    val weekEnd: String,                                                // "yyyy-MM-dd" (일요일)
    val totalTapCount: Int,
    val dailyTapCounts: List<InsightDailyTapCount> = emptyList(),        // 활동 기록 - 요일별 막대
    val categoryTapCounts: List<InsightCategoryTapCount> = emptyList(),  // 요일별 막대 카테고리 색 매핑용
    val buttonTapCounts: List<InsightButtonTapCount> = emptyList(),      // 기록 비율 - 버튼별 목록
    val peakDay: String? = null,                                        // 요약 카드 - 많이 기록한 요일 ("수요일")
    val peakTimeSlot: String? = null,                                   // 요약 카드 - 자주 기록한 시간대 ("아침")
    val peakTimeSlotSimple: String? = null,                             // 압축 표기용 ("오전")
    val topButton: InsightTopButton? = null,                            // 요약 카드 - 가장 많은 기록
    val prevWeekComparison: InsightPrevWeekComparison? = null           // 지난주 대비 기록 변화
)

/** 활동 기록 막대 한 칸 — 하루치 기록 수 + 카테고리 구성 */
data class InsightDailyTapCount(
    val date: String,                        // "yyyy-MM-dd"
    val total: Int,
    val categories: Map<String, Int> = emptyMap()  // key: categoryId 문자열, value: 기록 수
)

/** 지난주 대비 기록 변화 */
data class InsightPrevWeekComparison(
    val prevTotalTapCount: Int,
    val changeRate: Double,             // 증감률, 예: 0.225 = +22.5%
    val prevTopButton: InsightTopButton?
)