package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.TeamButtonPermission
import com.solux.luxup.taptap.feature.team.model.TeamSettings

/** GET /api/teams/{team_id}/settings 응답 예시 */
val mockTeamSettings = TeamSettings(
    teamId = 1,
    teamName = "LUX-UP",
    teamImageUrl = null,
    iconName = "study",
    iconColor = "yellow",
    inviteCode = "SE4EDI",
    maxMember = 10,
    memberCount = 7,
    buttonCreatePermission = TeamButtonPermission.ANYONE,
    buttonEditPermission = TeamButtonPermission.CREATOR_OR_LEADER,
    buttonDeletePermission = TeamButtonPermission.LEADER_ONLY,
    ownerUserId = 1,
    notificationEnabled = true,
    isDeleting = false,
    scheduledDeletionAt = null,
)

/** 배너 미리보기/테스트용 — 삭제 유예 중인 상태 */
val mockTeamSettingsDeleting = mockTeamSettings.copy(
    isDeleting = true,
    scheduledDeletionAt = java.time.LocalDateTime.now().plusHours(10).toString(),
)

/** 팀장으로 보고 있을 때의 내 userId */
const val MOCK_OWNER_USER_ID = 1L

/** 일반 멤버로 보고 있을 때의 내 userId */
const val MOCK_MEMBER_USER_ID = 3L