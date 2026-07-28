package com.solux.luxup.taptap.feature.home.template.data

import com.solux.luxup.taptap.feature.home.template.model.OnboardingTemplate
import com.solux.luxup.taptap.feature.home.template.model.TemplateButtonSuggestion

/** GET /api/templates 응답 목데이터 (Preview용) */
val mockOnboardingTemplates = listOf(
    OnboardingTemplate(
        templateId = 1,
        templateType = "memory",
        templateName = "잊지 않기 (Keep up)",
        description = "바쁜 일상 속 놓치기 쉬운 것들을 기억해요",
    ),
    OnboardingTemplate(
        templateId = 2,
        templateType = "self_care",
        templateName = "나 챙기기 (Self care)",
        description = "소중한 내 몸과 마음의 리듬을 기억해요",
    ),
    OnboardingTemplate(
        templateId = 3,
        templateType = "productivity",
        templateName = "성장하기",
        description = "배우고 나아가는 순간들을 기억해요",
    ),
)

/** GET /api/templates/1/recommendations 응답 목데이터 (Preview용) */
val mockTemplateSuggestionsMemory = listOf(
    TemplateButtonSuggestion(1, 1, "침구류 세탁하기", "clean", "indigo", "집"),
    TemplateButtonSuggestion(2, 1, "잠옷 세탁하기", "clothes", "blue", "집"),
    TemplateButtonSuggestion(3, 1, "냉장고 정리하기", "clean", "blue", "집"),
    TemplateButtonSuggestion(4, 1, "쓰레기 버리기", "clean", "darkgrey", "집"),
    TemplateButtonSuggestion(9, 1, "카드값 납부", "pay", "indigo", "생활"),
    TemplateButtonSuggestion(10, 1, "보험료 납부", "pay", "purple", "생활"),
    TemplateButtonSuggestion(16, 1, "가스불 확인", "fire", "red", "안전"),
    TemplateButtonSuggestion(17, 1, "콘센트 확인", "lightning", "yellow", "안전"),
    TemplateButtonSuggestion(22, 1, "약 먹기", "medicine", "red", "건강"),
    TemplateButtonSuggestion(23, 1, "영양제 먹기", "medicine", "blue", "건강"),
)