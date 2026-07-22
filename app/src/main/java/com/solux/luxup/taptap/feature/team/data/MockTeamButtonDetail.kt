package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.feature.team.model.ButtonRecord
import com.solux.luxup.taptap.feature.team.model.MemberProfile
import com.solux.luxup.taptap.feature.team.model.MyButtonPermission
import com.solux.luxup.taptap.feature.team.model.TeamButtonDetail

/**
 * GET /api/teams/{team_id}/buttons/{team_button_id} 응답 목데이터.
 * 실 API 연동 시 삭제 대상.
 */
object MockTeamButtonDetail {

    val detail = TeamButtonDetail(
        teamButtonId = 1L,
        teamId = 1L,
        buttonName = "기획서 업데이트",
        iconName = "document",
        iconColor = "yellow",
        description = "기획서 업데이트를 기록합니다.",
        tapPermission = "custom",
        isActive = true,
        createdBy = MemberProfile(1L, "누리", null),
        myPermission = MyButtonPermission(
            hasTapPermission = true,
            permissionStatus = "granted",
            isNotificationEnabled = true,
        ),
        categoryId = 1L,
        categoryName = "기획",
        allowedUserIds = listOf(1L, 2L, 4L),
        latestRecord = ButtonRecord(
            recordedAt = "2025-05-23T11:41:00",
            recordedBy = listOf(MemberProfile(1L, "누리", null)),
            recordedByCount = 1,
        ),
        createdAt = "2025-05-01T09:00:00",
        updatedAt = "2025-05-23T14:32:00",
    )
}