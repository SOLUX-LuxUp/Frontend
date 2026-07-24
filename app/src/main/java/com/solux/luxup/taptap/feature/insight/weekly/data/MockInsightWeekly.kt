package com.solux.luxup.taptap.feature.insight.weekly.data

import com.solux.luxup.taptap.feature.insight.daily.model.InsightButtonTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightTopButton
import com.solux.luxup.taptap.feature.insight.weekly.model.InsightDailyTapCount
import com.solux.luxup.taptap.feature.insight.weekly.model.InsightPrevWeekComparison
import com.solux.luxup.taptap.feature.insight.weekly.model.InsightWeekly

/** 위클리 인사이트 목데이터 — 시안(레포트 Weekly) 기준 숫자 */
val MockInsightWeekly = InsightWeekly(
    weekStart = "2026-05-04",
    weekEnd = "2026-05-10",
    totalTapCount = 88,
    dailyTapCounts = listOf(
        InsightDailyTapCount("2026-05-04", 10, mapOf("10" to 6, "20" to 4)),
        InsightDailyTapCount("2026-05-05", 16, mapOf("10" to 8, "20" to 6, "30" to 2)),
        InsightDailyTapCount("2026-05-06", 14, mapOf("10" to 5, "20" to 5, "30" to 4)),
        InsightDailyTapCount("2026-05-07", 11, mapOf("10" to 6, "20" to 5)),
        InsightDailyTapCount("2026-05-08", 18, mapOf("10" to 8, "20" to 7, "30" to 3)),
        InsightDailyTapCount("2026-05-09", 7, mapOf("10" to 5, "20" to 2)),
        InsightDailyTapCount("2026-05-10", 12, mapOf("10" to 6, "20" to 6)),
    ),
    categoryTapCounts = listOf(
        InsightCategoryTapCount(10L, "건강", 44, 44 / 88.0, "#4C8DFF"),
        InsightCategoryTapCount(20L, "자기계발", 35, 35 / 88.0, "#FFB84C"),
        InsightCategoryTapCount(30L, "여가", 9, 9 / 88.0, "#FF6B6B"),
    ),
    buttonTapCounts = listOf(
        InsightButtonTapCount(1L, "물 마시기", 34, 34 / 88.0, "drink", "#4C9AFF", 10L, "건강"),
        InsightButtonTapCount(2L, "경제공부", 20, 20 / 88.0, "book", "#4C8DFF", 20L, "자기계발"),
        InsightButtonTapCount(3L, "필기하기", 15, 15 / 88.0, "pencil", "#FF5C5C", 20L, "자기계발"),
        InsightButtonTapCount(4L, "연락하기", 10, 10 / 88.0, "person", "#4C8DFF", 10L, "건강"),
        InsightButtonTapCount(5L, "일기쓰기", 9, 9 / 88.0, "note", "#FFC107", 30L, "여가"),
    ),
    peakDay = "수요일",
    peakTimeSlot = "아침",
    peakTimeSlotSimple = "오전",
    topButton = InsightTopButton(
        buttonId = 1L,
        buttonName = "물 마시기",
        iconName = "drink",
        iconColor = "#4C9AFF",
        count = 34
    ),
    prevWeekComparison = InsightPrevWeekComparison(
        prevTotalTapCount = 70,
        changeRate = (88 - 70) / 70.0,
        prevTopButton = InsightTopButton(
            buttonId = 3L,
            buttonName = "필기하기",
            iconName = "pencil",
            iconColor = "#FF5C5C",
            count = 22
        )
    )
)