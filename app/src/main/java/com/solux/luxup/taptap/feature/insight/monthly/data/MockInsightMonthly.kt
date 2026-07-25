package com.solux.luxup.taptap.feature.insight.monthly.data

import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthly
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyComparison
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyRankedButton
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightMonthlyTopCategory
import com.solux.luxup.taptap.feature.insight.monthly.model.InsightTimeSlotStat

/** 먼슬리 인사이트 목데이터 — 시안(레포트 Monthly) 기준 숫자 */
val MockInsightMonthly = InsightMonthly(
    year = 2026,
    month = 5,
    totalTapCount = 349,
    dailyTapCounts = mapOf(
        "2026-05-01" to 8,
        "2026-05-04" to 15,
        "2026-05-05" to 42,
        "2026-05-06" to 12,
        "2026-05-09" to 38,
        "2026-05-10" to 6,
        "2026-05-12" to 45,
        "2026-05-14" to 20,
        "2026-05-20" to 25,
        "2026-05-21" to 9,
        "2026-05-25" to 18,
        "2026-05-28" to 33,
        "2026-05-31" to 5,
    ),
    categoryTapCounts = listOf(
        InsightCategoryTapCount(1L, "자기관리", 93, 93 / 207.0),
        InsightCategoryTapCount(2L, "운동", 58, 58 / 207.0),
        InsightCategoryTapCount(3L, "공부", 31, 31 / 207.0),
        InsightCategoryTapCount(4L, "기억", 14, 14 / 207.0),
        InsightCategoryTapCount(5L, "기타", 11, 11 / 207.0),
    ),
    top3Buttons = listOf(
        InsightMonthlyRankedButton(1, 1L, "물마시기", 87),
        InsightMonthlyRankedButton(2, 2L, "설거지 하기", 54),
        InsightMonthlyRankedButton(3, 3L, "러닝", 19),
    ),
    topCategory = InsightMonthlyTopCategory(categoryId = 1L, categoryName = "자기관리", count = 131),
    busiestDay = "2026-05-12",
    weekdayRatio = 0.61,
    weekendRatio = 0.39,
    timeSlotCategory = mapOf(
        "새벽" to InsightTimeSlotStat(count = 9, ratio = 0.09, categoryId = 1L, categoryName = "자기관리"),
        "아침" to InsightTimeSlotStat(count = 28, ratio = 0.28, categoryId = 4L, categoryName = "기억"),
        "점심" to InsightTimeSlotStat(count = 16, ratio = 0.16, categoryId = 2L, categoryName = "운동"),
        "저녁" to InsightTimeSlotStat(count = 32, ratio = 0.32, categoryId = 3L, categoryName = "공부"),
        "밤" to InsightTimeSlotStat(count = 13, ratio = 0.13, categoryId = 5L, categoryName = "기타"),
    ),
    prevMonthComparison = InsightMonthlyComparison(
        prevTotalTapCount = 416,
        changeRate = -0.17,
        prevTop5Buttons = listOf(
            InsightMonthlyRankedButton(1, 1L, "물마시기", 87),
            InsightMonthlyRankedButton(2, 2L, "설거지 하기", 54),
            InsightMonthlyRankedButton(3, 3L, "러닝", 19),
            InsightMonthlyRankedButton(4, 4L, "영단어 외우기", 13),
            InsightMonthlyRankedButton(5, 5L, "독서", 12),
        ),
        currentTop5Buttons = listOf(
            InsightMonthlyRankedButton(1, 1L, "물마시기", 56),
            InsightMonthlyRankedButton(2, 3L, "러닝", 35),
            InsightMonthlyRankedButton(3, 6L, "물 주기", 20),
            InsightMonthlyRankedButton(4, 4L, "영단어 외우기", 10),
            InsightMonthlyRankedButton(5, 5L, "독서", 9),
        )
    )
)