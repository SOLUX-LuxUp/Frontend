package com.solux.luxup.taptap.feature.insight.lifestyle.model

/**
 * 나의 라이프 스타일 ("이 달의 라이프 스타일" 진입 화면)
 * GET /api/insights/lifestyle
 */
data class InsightLifestyle(
    val analysisAvailable: Boolean,
    val lifestyleLabel: String,
    val lifestyleCaption: String,
    val analysisButtons: List<InsightLifestyleAnalysisButton> = emptyList(), // 분석에 사용한 버튼
    val recommendations: List<InsightLifestyleRecommendation> = emptyList()
)

/** 분석에 사용한 버튼 한 개 */
data class InsightLifestyleAnalysisButton(
    val buttonId: Long,
    val buttonName: String,
    val iconName: String?,
    val iconColor: String?
)

/** 라이프 스타일 추천 한 건 — 버튼 추가(ADD) 또는 삭제(DELETE) 제안 */
data class InsightLifestyleRecommendation(
    val recId: Long,
    val recType: String,                  // "ADD" | "DELETE"
    // ADD 전용
    val suggestedButtonName: String? = null,
    val suggestedIconName: String? = null,
    val suggestedIconColor: String? = null,
    // DELETE 전용
    val buttonId: Long? = null,
    val buttonName: String? = null,
    val lastRecordedAt: String? = null    // ISO 문자열, DELETE 전용
)
