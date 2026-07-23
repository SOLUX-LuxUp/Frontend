package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.TeamCreateResult
import com.solux.luxup.taptap.feature.team.model.TeamTemplate

/** POST /api/teams 응답 예시 */
val mockTeamCreateResult = TeamCreateResult(
    teamId = 1,
    teamName = "새로운 팀1",
    teamImageUrl = null,
    iconName = "heart",
    iconColor = "cyan",
    inviteCode = "SE4EDI",
    maxMember = 10,
    ownerUserId = 1,
    createdAt = "2025-05-23T14:32:00",
)

/** GET /api/team-templates 응답 */
val mockTeamTemplates = listOf(
    TeamTemplate(
        templateId = 1,
        templateType = "together",
        templateName = "함께하기",
        description = "일상의 순간들을 공유해요",
        subDescription = "가족/친구/연인",
    ),
    TeamTemplate(
        templateId = 2,
        templateType = "collaborate",
        templateName = "협력하기",
        description = "함께 나아가는 시간을 공유해요",
        subDescription = "프로젝트/목표 달성",
    ),
)