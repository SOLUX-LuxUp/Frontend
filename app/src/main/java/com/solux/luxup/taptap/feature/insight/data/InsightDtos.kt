package com.solux.luxup.taptap.feature.insight.data

import kotlinx.serialization.Serializable

// ---- 공통 (일간/주간/월간에서 공유) ----

@Serializable
data class TopButtonDto(
    val buttonId: Long,
    val buttonName: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val count: Int = 0,
)

@Serializable
data class CategoryTapCountDto(
    val categoryId: Long,
    val categoryName: String? = null,
    val count: Int = 0,
    val ratio: Double = 0.0,
)

@Serializable
data class ButtonTapCountDto(
    val buttonId: Long,
    val buttonName: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val count: Int = 0,
    val ratio: Double = 0.0,
)

// ---- 데일리 (7.1 GET /api/insights/daily) ----

@Serializable
data class PersonalInsightTimelineItemDto(
    val recordId: Long,
    val buttonId: Long,
    val buttonName: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val recordedAt: String? = null,
    val memo: String? = null,
    val emoji: String? = null,
)

@Serializable
data class InsightDailyResponseDto(
    val targetDate: String,
    val totalTapCount: Int = 0,
    val topButton: TopButtonDto? = null,
    val peakTimeSlot: String? = null,
    val categoryTapCounts: List<CategoryTapCountDto> = emptyList(),
    val buttonTapCounts: List<ButtonTapCountDto> = emptyList(),
    val timeline: List<PersonalInsightTimelineItemDto> = emptyList(),
)

// ---- 위클리 (7.2 GET /api/insights/weekly) ----

@Serializable
data class DailyTapCountDto(
    val date: String,
    val total: Int = 0,
    val categories: Map<String, Int> = emptyMap(),
)

@Serializable
data class WeeklyTopButtonDto(
    val buttonId: Long,
    val buttonName: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val count: Int = 0,
)

@Serializable
data class PrevWeekComparisonDto(
    val prevTotalTapCount: Int = 0,
    val changeRate: Double = 0.0,
    val prevTopButton: WeeklyTopButtonDto? = null,
)

@Serializable
data class InsightWeeklyResponseDto(
    val weekStart: String,
    val weekEnd: String,
    val totalTapCount: Int = 0,
    val dailyTapCounts: List<DailyTapCountDto> = emptyList(),
    val categoryTapCounts: List<CategoryTapCountDto> = emptyList(),
    val buttonTapCounts: List<ButtonTapCountDto> = emptyList(),
    val peakDay: String? = null,
    val peakTimeSlot: String? = null,
    val topButton: WeeklyTopButtonDto? = null,
    val prevWeekComparison: PrevWeekComparisonDto? = null,
)

// ---- 먼슬리 (7.3 GET /api/insights/monthly) ----

@Serializable
data class RankedButtonDto(
    val rank: Int,
    val buttonId: Long,
    val buttonName: String? = null,
    val count: Int = 0,
)

@Serializable
data class TopCategoryDto(
    val categoryId: Long,
    val categoryName: String? = null,
    val count: Int = 0,
)

@Serializable
data class TimeSlotCategoryEntryDto(
    val count: Int = 0,
    val ratio: Double = 0.0,
    val categoryId: Long? = null,
    val categoryName: String? = null,
)

@Serializable
data class PrevMonthComparisonDto(
    val prevTotalTapCount: Int = 0,
    val changeRate: Double = 0.0,
    val prevTop5Buttons: List<RankedButtonDto> = emptyList(),
    val currentTop5Buttons: List<RankedButtonDto> = emptyList(),
)

@Serializable
data class InsightMonthlyResponseDto(
    val year: Int,
    val month: Int,
    val totalTapCount: Int = 0,
    val dailyTapCounts: Map<String, Int> = emptyMap(),
    val categoryTapCounts: List<CategoryTapCountDto> = emptyList(),
    val buttonTapCounts: List<ButtonTapCountDto> = emptyList(),
    val top3Buttons: List<RankedButtonDto> = emptyList(),
    val topCategory: TopCategoryDto? = null,
    val busiestDay: String? = null,
    val weekdayRatio: Double = 0.0,
    val weekendRatio: Double = 0.0,
    val timeSlotCategory: Map<String, TimeSlotCategoryEntryDto> = emptyMap(),
    val prevMonthComparison: PrevMonthComparisonDto? = null,
)

// ---- 라이프스타일 추천 (12. GET/PATCH /api/lifestyle-recommendations) ----

@Serializable
data class AnalysisButtonDto(
    val buttonId: Long,
    val buttonName: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
)

@Serializable
data class LifestyleRecommendationDto(
    val recId: Long,
    val recType: String? = null,
    val suggestedButtonName: String? = null,
    val suggestedIconName: String? = null,
    val suggestedIconColor: String? = null,
    val buttonId: Long? = null,
    val buttonName: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val lastRecordedAt: String? = null,
)

@Serializable
data class LifestyleRecommendationsResponseDto(
    val analysisAvailable: Boolean = false,
    val lifestyleLabel: String? = null,
    val lifestyleCaption: String? = null,
    val analysisButtons: List<AnalysisButtonDto> = emptyList(),
    val recommendations: List<LifestyleRecommendationDto> = emptyList(),
)

@Serializable
data class LifestyleRecommendationActionRequestDto(
    val action: String,
)

@Serializable
data class LifestyleRecommendationActionResponseDto(
    val recId: Long,
    val recType: String? = null,
    val action: String? = null,
    val createdButtonId: Long? = null,
)