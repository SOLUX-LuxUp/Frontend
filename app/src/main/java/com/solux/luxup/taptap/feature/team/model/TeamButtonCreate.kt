package com.solux.luxup.taptap.feature.team.model

import com.solux.luxup.taptap.core.ui.theme.IconColor

/**
 * 팀 공유 버튼 (기능명세서 8.1.1 ~ 8.1.4)
 * POST /api/teams/{team_id}/buttons
 */

/** tapPermission — all / custom 2종 (owner_only 옵션 없음) */
enum class TapPermission(val value: String, val label: String) {
    ALL("all", "모든 멤버"),
    CUSTOM("custom", "일부 멤버만"),
    ;

    companion object {
        fun from(value: String?): TapPermission = entries.firstOrNull { it.value == value } ?: ALL
    }
}

data class TeamButtonCategory(
    val categoryId: Long?,
    val name: String,
) {
    companion object {
        /** categoryId = null → No Category */
        val None = TeamButtonCategory(null, "No Category")
    }
}

/**
 * 생성 폼 상태.
 * 모든 필드가 optional이라 비워두면 서버 기본값(이름 "새로운 버튼", 아이콘·색상 랜덤)이 들어감.
 */
data class TeamButtonForm(
    val name: String = "",
    val iconName: String = "",
    val iconColor: IconColor = IconColor.DEFAULT,
    val description: String = "",
    val category: TeamButtonCategory = TeamButtonCategory.None,
    val tapPermission: TapPermission = TapPermission.ALL,
    val allowedUserIds: List<Long> = emptyList(),
) {
    /** custom인데 허용 멤버가 0명이면 아무도 못 누르는 버튼이 되므로 막는다 */
    val canSubmit: Boolean
        get() = tapPermission == TapPermission.ALL || allowedUserIds.isNotEmpty()

    val allowedMemberCount: Int
        get() = allowedUserIds.size

    companion object {
        const val NAME_MAX = 15 // TODO(백엔드): 이름 최대 길이 명시 요청
        const val DESCRIPTION_MAX = 50
    }
}