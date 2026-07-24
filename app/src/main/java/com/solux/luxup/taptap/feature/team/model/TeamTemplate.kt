package com.solux.luxup.taptap.feature.team.model

/**
 * POST /api/teams 응답.
 * 초대코드 공유 화면 → 템플릿 선택 화면까지 들고 다닌다.
 */
data class TeamCreateResult(
    val teamId: Long,
    val teamName: String,
    val teamImageUrl: String?,
    val iconName: String?,
    val iconColor: String?,
    val inviteCode: String,
    val maxMember: Int,
    val ownerUserId: Long,
    val createdAt: String,
)

/**
 * GET /api/team-templates
 * 팀 생성 직후 "무엇을 함께 기억하고 싶나요?" 화면에 노출.
 */
data class TeamTemplate(
    val templateId: Long,
    val templateType: String,      // "together" | "collaborate"
    val templateName: String,      // "함께하기" | "협력하기"
    val description: String,       // "일상의 순간들을 공유해요"
    val subDescription: String,    // "가족/친구/연인"
)

/**
 * GET /api/teams/{team_id}/template
 * 활동 탭 초기 화면 분기 및 "+" → 빠르게 생성 노출 여부 판단에 사용.
 */
data class TeamTemplateStatus(
    val hasSelectedTemplate: Boolean,
    val isSkipped: Boolean,
    val templateId: Long?,
    val templateType: String?,
    val templateName: String?,
) {
    /** 템플릿 선택 화면을 띄울지 — 아직 결정하지 않은 팀만 */
    val needsTemplateSelection: Boolean
        get() = !hasSelectedTemplate && !isSkipped

    /** "+" 눌렀을 때 [직접 생성 / 빠르게 생성] 분기를 보여줄지 */
    val canQuickCreate: Boolean
        get() = hasSelectedTemplate
}