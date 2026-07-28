package com.solux.luxup.taptap.feature.home.template.model

/**
 * GET /api/templates
 * 온보딩 "어떤 일상을 기록하고싶나요?" 화면의 카드 목록.
 */
data class OnboardingTemplate(
    val templateId: Long,
    val templateType: String,
    val templateName: String,
    val description: String,
)

/**
 * GET /api/templates/{template_id}/recommendations 의 버튼 한 개.
 * categories로 묶여 내려오지만, 카테고리 탭 UI에서 다루기 쉽도록 평탄화해서 들고 있는다.
 */
data class TemplateButtonSuggestion(
    val presetId: Long,
    val templateId: Long,
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
    val categoryName: String,
)

/** GET /api/templates/{template_id}/recommendations */
data class TemplatePreview(
    val templateId: Long,
    val templateName: String,
    val suggestions: List<TemplateButtonSuggestion>,
)