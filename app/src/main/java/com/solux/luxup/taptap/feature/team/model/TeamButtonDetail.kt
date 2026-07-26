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
    /** 팀 설정 buttonEditPermission + 생성자/팀장 여부 반영 */
    val canEdit: Boolean,
    /** 팀 설정 buttonDeletePermission + 생성자/팀장 여부 반영 */
    val canDelete: Boolean,
    /** 관리자/비관리자 화면 분기용 */
    val isTeamOwner: Boolean,
    val categoryId: Long?,
    val categoryName: String?,
    val allowedUserIds: List<Long>,
    val latestRecord: ButtonRecord?,
    val createdAt: String,
    val updatedAt: String,
) {
    /**
     * 버튼 정보 화면의 관리자 화면(권한 승인·거부 탭)을 볼 수 있는지.
     * 팀장이거나 이 버튼을 만든 사람.
     */
    fun isManager(currentUserId: Long): Boolean =
        isTeamOwner || createdBy.userId == currentUserId

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
)

/**
 * 탭 권한 요청 목록 조회 응답.
 * TODO(백엔드): URL 확인 필요
 */
data class TeamButtonPermissionRequest(
    val userId: Long,
    val displayName: String,
    val profileImageUrl: String?,
    val requestedAt: String,
)

/** myPermission.permissionStatus 값 */
object PermissionStatus {
    const val GRANTED = "granted"
    const val PENDING = "pending"
    const val DENIED = "denied"
}