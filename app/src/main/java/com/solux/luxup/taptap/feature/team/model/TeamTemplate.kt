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
 * 템플릿 선택 여부. 활동 탭 초기 화면 분기에 사용.
 *
 * 건너뛰기와 "아직 선택 안 함"이 서버에서 구분되지 않음 (백엔드 확인 요청함)
 */
data class TeamTemplateStatus(
    val hasSelectedTemplate: Boolean,
    val templateId: Long?,
    val templateType: String?,
    val templateName: String?,
)