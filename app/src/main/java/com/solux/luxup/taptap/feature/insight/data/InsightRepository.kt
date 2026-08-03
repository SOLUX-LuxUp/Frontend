package com.solux.luxup.taptap.feature.insight.data

import com.solux.luxup.taptap.core.network.ApiCallHandler
import com.solux.luxup.taptap.feature.insight.daily.model.InsightButtonTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightDaily
import com.solux.luxup.taptap.feature.insight.daily.model.InsightTimelineItem
import com.solux.luxup.taptap.feature.insight.daily.model.InsightTopButton
import com.solux.luxup.taptap.feature.insight.lifestyle.model.InsightLifestyle
import com.solux.luxup.taptap.feature.insight.lifestyle.model.InsightLifestyleAnalysisButton
import com.solux.luxup.taptap.feature.insight.lifestyle.model.InsightLifestyleRecommendation
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthly
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyComparison
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyRankedButton
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyTopCategory
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightTimeSlotStat
import com.solux.luxup.taptap.feature.insight.weekly.model.InsightDailyTapCount
import com.solux.luxup.taptap.feature.insight.weekly.model.InsightPrevWeekComparison
import com.solux.luxup.taptap.feature.insight.weekly.model.InsightWeekly
import javax.inject.Inject
import javax.inject.Singleton

/** 개인 인사이트(7. 인사이트) — 일간/주간/월간 3개 엔드포인트 */
@Singleton
class InsightRepository @Inject constructor(
    private val insightApi: InsightApi,
    private val apiCallHandler: ApiCallHandler,
) {
    suspend fun getDailyInsight(date: String? = null): Result<InsightDaily> =
        apiCallHandler.execute { insightApi.getDailyInsight(date) }.mapCatching { it.toModel() }

    suspend fun getWeeklyInsight(weekStart: String? = null): Result<InsightWeekly> =
        apiCallHandler.execute { insightApi.getWeeklyInsight(weekStart) }.mapCatching { it.toModel() }

    suspend fun getMonthlyInsight(year: Int? = null, month: Int? = null): Result<InsightMonthly> =
        apiCallHandler.execute { insightApi.getMonthlyInsight(year, month) }.mapCatching { it.toModel() }

    // ---- 라이프스타일 추천 ----

    suspend fun getLifestyleRecommendations(): Result<InsightLifestyle> =
        apiCallHandler.execute { insightApi.getLifestyleRecommendations() }.mapCatching { it.toModel() }

    /** action: "accept"(수락) | "dismiss"(무시) */
    suspend fun processLifestyleRecommendation(recId: Long, action: String): Result<Unit> =
        apiCallHandler.execute {
            insightApi.processLifestyleRecommendationAction(recId, LifestyleRecommendationActionRequestDto(action))
        }.map { }
}

private fun TopButtonDto.toModel() = InsightTopButton(
    buttonId = buttonId,
    buttonName = buttonName.orEmpty(),
    iconName = iconName,
    iconColor = iconColor,
    count = count,
)

private fun WeeklyTopButtonDto.toModel() = InsightTopButton(
    buttonId = buttonId,
    buttonName = buttonName.orEmpty(),
    iconName = iconName,
    iconColor = iconColor,
    count = count,
)

private fun CategoryTapCountDto.toModel() = InsightCategoryTapCount(
    categoryId = categoryId,
    categoryName = categoryName.orEmpty(),
    count = count,
    ratio = ratio,
)

private fun ButtonTapCountDto.toModel() = InsightButtonTapCount(
    buttonId = buttonId,
    buttonName = buttonName.orEmpty(),
    count = count,
    ratio = ratio,
    iconName = iconName,
    iconColor = iconColor,
)

private fun PersonalInsightTimelineItemDto.toModel() = InsightTimelineItem(
    recordId = recordId,
    buttonId = buttonId,
    buttonName = buttonName.orEmpty(),
    recordedAt = recordedAt.orEmpty(),
    memo = memo,
    emoji = emoji,
    iconName = iconName,
    iconColor = iconColor,
)

private fun InsightDailyResponseDto.toModel() = InsightDaily(
    targetDate = targetDate,
    totalTapCount = totalTapCount,
    topButton = topButton?.toModel(),
    peakTimeSlot = peakTimeSlot,
    peakTimeSlotSimple = null, // API 미제공, 화면에서도 미사용
    categoryTapCounts = categoryTapCounts.map { it.toModel() },
    buttonTapCounts = buttonTapCounts.map { it.toModel() },
    timeline = timeline.map { it.toModel() },
)

private fun DailyTapCountDto.toModel() = InsightDailyTapCount(
    date = date,
    total = total,
    categories = categories,
)

private fun PrevWeekComparisonDto.toModel() = InsightPrevWeekComparison(
    prevTotalTapCount = prevTotalTapCount,
    changeRate = changeRate,
    prevTopButton = prevTopButton?.toModel(),
)

private fun InsightWeeklyResponseDto.toModel() = InsightWeekly(
    weekStart = weekStart,
    weekEnd = weekEnd,
    totalTapCount = totalTapCount,
    dailyTapCounts = dailyTapCounts.map { it.toModel() },
    categoryTapCounts = categoryTapCounts.map { it.toModel() },
    buttonTapCounts = buttonTapCounts.map { it.toModel() },
    peakDay = peakDay,
    peakTimeSlot = peakTimeSlot,
    peakTimeSlotSimple = null, // API 미제공, 화면에서도 미사용
    topButton = topButton?.toModel(),
    prevWeekComparison = prevWeekComparison?.toModel(),
)

private fun RankedButtonDto.toModel(iconColor: String? = null) = InsightMonthlyRankedButton(
    rank = rank,
    buttonId = buttonId,
    buttonName = buttonName.orEmpty(),
    count = count,
    iconColor = iconColor,
)

private fun TopCategoryDto.toModel() = InsightMonthlyTopCategory(
    categoryId = categoryId,
    categoryName = categoryName.orEmpty(),
    count = count,
)

private fun TimeSlotCategoryEntryDto.toModel() = InsightTimeSlotStat(
    count = count,
    ratio = ratio,
    categoryId = categoryId,
    categoryName = categoryName,
)

private fun PrevMonthComparisonDto.toModel() = InsightMonthlyComparison(
    prevTotalTapCount = prevTotalTapCount,
    changeRate = changeRate,
    prevTop5Buttons = prevTop5Buttons.map { it.toModel() },
    currentTop5Buttons = currentTop5Buttons.map { it.toModel() },
)

// top3Buttons 응답 자체엔 iconColor가 없어, 같은 응답의 buttonTapCounts에서 buttonId로 매칭해 채운다
private fun InsightMonthlyResponseDto.toModel(): InsightMonthly {
    val iconColorByButtonId = buttonTapCounts.associate { it.buttonId to it.iconColor }
    return InsightMonthly(
        year = year,
        month = month,
        totalTapCount = totalTapCount,
        dailyTapCounts = dailyTapCounts,
        categoryTapCounts = categoryTapCounts.map { it.toModel() },
        buttonTapCounts = buttonTapCounts.map { it.toModel() },
        top3Buttons = top3Buttons.map { it.toModel(iconColorByButtonId[it.buttonId]) },
        topCategory = topCategory?.toModel(),
        busiestDay = busiestDay,
        weekdayRatio = weekdayRatio,
        weekendRatio = weekendRatio,
        timeSlotCategory = timeSlotCategory.mapValues { it.value.toModel() },
        prevMonthComparison = prevMonthComparison?.toModel(),
    )
}

private fun AnalysisButtonDto.toModel() = InsightLifestyleAnalysisButton(
    buttonId = buttonId,
    buttonName = buttonName.orEmpty(),
    iconName = iconName,
    iconColor = iconColor,
)

private fun LifestyleRecommendationDto.toModel() = InsightLifestyleRecommendation(
    recId = recId,
    recType = recType.orEmpty(),
    suggestedButtonName = suggestedButtonName,
    suggestedIconName = suggestedIconName,
    suggestedIconColor = suggestedIconColor,
    buttonId = buttonId,
    buttonName = buttonName,
    lastRecordedAt = lastRecordedAt,
)

private fun LifestyleRecommendationsResponseDto.toModel() = InsightLifestyle(
    analysisAvailable = analysisAvailable,
    lifestyleLabel = lifestyleLabel.orEmpty(),
    lifestyleCaption = lifestyleCaption.orEmpty(),
    analysisButtons = analysisButtons.map { it.toModel() },
    recommendations = recommendations.map { it.toModel() },
)