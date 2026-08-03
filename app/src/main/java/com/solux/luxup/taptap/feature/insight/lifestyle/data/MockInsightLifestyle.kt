package com.solux.luxup.taptap.feature.insight.lifestyle.data

import com.solux.luxup.taptap.feature.insight.lifestyle.model.InsightLifestyle
import com.solux.luxup.taptap.feature.insight.lifestyle.model.InsightLifestyleAnalysisButton
import com.solux.luxup.taptap.feature.insight.lifestyle.model.InsightLifestyleRecommendation

/** 나의 라이프 스타일 목데이터 — API 명세서 예시 응답 기준 */
val MockInsightLifestyle = InsightLifestyle(
    analysisAvailable = true,
    lifestyleLabel = "규칙적인 기억",
    lifestyleCaption = "규칙적으로 기록하며 리듬을 만들어가고 있어요.",
    analysisButtons = listOf(
        InsightLifestyleAnalysisButton(1L, "물 마시기", "drink", "#4D96FF"),
        InsightLifestyleAnalysisButton(2L, "약 먹기", "medicine", "#FF6B6B"),
        InsightLifestyleAnalysisButton(3L, "러닝", "sport1", "#4D96FF"),
        InsightLifestyleAnalysisButton(4L, "일기쓰기", "note", "#FF9F45"),
        InsightLifestyleAnalysisButton(5L, "공부", "labtop", "#FF9F45"),
    ),
    recommendations = listOf(
        InsightLifestyleRecommendation(
            recId = 1,
            recType = "ADD",
            suggestedButtonName = "독서",
            suggestedIconName = "book",
            suggestedIconColor = "#FF9F45"
        ),
        InsightLifestyleRecommendation(
            recId = 2,
            recType = "ADD",
            suggestedButtonName = "스트레칭",
            suggestedIconName = "sun",
            suggestedIconColor = "#FF9F45"
        ),
        InsightLifestyleRecommendation(
            recId = 3,
            recType = "ADD",
            suggestedButtonName = "식사하기",
            suggestedIconName = "food",
            suggestedIconColor = "#FF9F45"
        ),
        InsightLifestyleRecommendation(
            recId = 4,
            recType = "ADD",
            suggestedButtonName = "문단속",
            suggestedIconName = "lock",
            suggestedIconColor = "#FF9F45"
        ),
        InsightLifestyleRecommendation(
            recId = 6,
            recType = "DELETE",
            buttonId = 4,
            buttonName = "텀블러 사용",
            iconName = "cup",
            iconColor = "#4D96FF",
            lastRecordedAt = "2026-04-03T09:12:00"
        ),
        InsightLifestyleRecommendation(
            recId = 7,
            recType = "DELETE",
            buttonId = 5,
            buttonName = "기타 연습",
            iconName = "guitar",
            iconColor = "#FF9F45",
            lastRecordedAt = "2026-04-01T21:03:00"
        ),
        InsightLifestyleRecommendation(
            recId = 8,
            recType = "DELETE",
            buttonId = 6,
            buttonName = "비타민 먹기",
            iconName = "medicine",
            iconColor = "#FF6B6B",
            lastRecordedAt = "2026-03-25T21:03:00"
        ),
    )
)