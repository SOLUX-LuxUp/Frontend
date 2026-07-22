package com.solux.luxup.taptap.feature.team.model

import com.solux.luxup.taptap.core.ui.theme.IconColor

/**
 * GET /api/teams/{team_id}/buttons/{team_button_id} (8.1.3)
 * 버튼 정보 화면과 수정 화면의 초기값 소스.
 */
data class TeamButtonDetail(
    val teamButtonId: Long,
    val teamId: Long,
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
    val description: String?,
    val tapPermission: String,
    val isActive: Boolean,
    val createdBy: MemberProfile,
    val myPermission: MyButtonPermission,
    val categoryId: Long?,
    val categoryName: String?,
    val allowedUserIds: List<Long>,
    val latestRecord: ButtonRecord?,
    val createdAt: String,
    val updatedAt: String,
) {
    /** 수정 화면 초기값으로 변환 */
    fun toForm(): TeamButtonForm = TeamButtonForm(
        name = buttonName,
        iconName = iconName,
        iconColor = IconColor.from(iconColor),
        description = description.orEmpty(),
        category = categoryId?.let {
            // 상세 응답은 색상·정렬 순서를 주지 않으므로 표시에 필요한 값만 채운다.
            // 드롭다운에서 다시 고르면 카테고리 목록 조회 결과로 교체된다.
            TeamButtonCategory(
                categoryId = it,
                categoryName = categoryName.orEmpty(),
                categoryColor = IconColor.DEFAULT,
                displayOrder = 0,
            )
        },
        tapPermission = TapPermission.from(tapPermission),
        allowedUserIds = allowedUserIds,
    )
}

/**
 * 내 권한 상태.
 * permissionStatus 값 종류는 연동 시 실제 응답으로 확인 필요 (granted / pending / denied / 미요청).
 */
data class MyButtonPermission(
    val hasTapPermission: Boolean,
    val permissionStatus: String?,
    val isNotificationEnabled: Boolean,
)