package com.solux.luxup.taptap.feature.insight.daily.data

import com.solux.luxup.taptap.feature.insight.daily.model.InsightButtonTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightCategoryTapCount
import com.solux.luxup.taptap.feature.insight.daily.model.InsightDaily
import com.solux.luxup.taptap.feature.insight.daily.model.InsightTimelineItem
import com.solux.luxup.taptap.feature.insight.daily.model.InsightTopButton
import java.time.LocalDateTime

/** 데일리 인사이트 목데이터 — 시안(레포트 Daily) 기준 숫자 */
val MockInsightDaily = InsightDaily(
    targetDate = "2026-07-07",
    totalTapCount = 55,
    topButton = InsightTopButton(
        buttonId = 1L,
        buttonName = "물 마시기",
        iconName = "drink",
        iconColor = "#4C9AFF",
        count = 17
    ),
    peakTimeSlot = "아침",
    peakTimeSlotSimple = "오전",
    categoryTapCounts = listOf(
        InsightCategoryTapCount(10L, "건강", 27, 0.491, "#4C8DFF"),
        InsightCategoryTapCount(20L, "자기계발", 28, 0.509, "#FFB84C"),
    ),
    buttonTapCounts = listOf(
        InsightButtonTapCount(1L, "물 마시기", 19, 19 / 55.0, "drink", "#4C9AFF", 10L, "건강"),
        InsightButtonTapCount(2L, "경제공부", 17, 17 / 55.0, "book", "#4C8DFF", 20L, "자기계발"),
        InsightButtonTapCount(3L, "필기하기", 10, 10 / 55.0, "pencil", "#FF5C5C", 20L, "자기계발"),
        InsightButtonTapCount(4L, "연락하기", 8, 8 / 55.0, "person", "#4C8DFF", 10L, "건강"),
        InsightButtonTapCount(5L, "일기쓰기", 1, 1 / 55.0, "note", "#FFC107", 20L, "자기계발"),
    ),
    timeline = listOf(
        InsightTimelineItem(
            recordId = 101L, buttonId = 1L, buttonName = "물 마시기",
            recordedAt = LocalDateTime.now().minusHours(3).toString(),
            iconName = "drink", iconColor = "#4C9AFF"
        ),
        InsightTimelineItem(
            recordId = 102L, buttonId = 2L, buttonName = "경제공부",
            recordedAt = LocalDateTime.now().minusHours(5).toString(),
            iconName = "book", iconColor = "#4C8DFF"
        ),
        InsightTimelineItem(
            recordId = 103L, buttonId = 3L, buttonName = "필기하기",
            recordedAt = LocalDateTime.now().minusHours(9).toString(),
            iconName = "pencil", iconColor = "#FF5C5C"
        ),
        InsightTimelineItem(
            recordId = 104L, buttonId = 6L, buttonName = "기획서 작성",
            recordedAt = LocalDateTime.now().minusHours(9).toString(),
            iconName = "note", iconColor = "#FFC107"
        ),
    )
)