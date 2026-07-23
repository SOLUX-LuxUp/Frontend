package com.solux.luxup.taptap.feature.team.model

/**
 * 팀 만들기 화면의 입력 상태.
 * POST /api/teams 요청 바디로 그대로 변환된다.
 *
 * 팀 프로필은 이미지(teamImageUrl) 또는 아이콘(iconName + iconColor) 택일.
 * 모든 필드가 서버에서 optional이라 비워둔 채로도 생성 가능하다.
 * (teamName 미전송 시 서버가 "새로운 팀 N" 자동 부여)
 */
data class TeamCreateForm(
    val teamName: String = "",
    val teamImageUrl: String? = null,
    val iconName: String? = null,
    val iconColor: String? = null,
    val maxMember: Int = DEFAULT_MAX_MEMBER,
) {
    companion object {
        const val DEFAULT_MAX_MEMBER = 10
        const val MAX_TEAM_NAME_LENGTH = 20
        /** 기획 확정: 아이콘·이미지 미설정 시 1번 아이콘 + 블루 */
        const val DEFAULT_ICON_NAME = "people"
        const val DEFAULT_ICON_COLOR = "blue"

        /** 기획 확정: 5단위 스텝, 5~30 */
        val MAX_MEMBER_OPTIONS = listOf(5, 10, 15, 20, 25, 30)
    }
}