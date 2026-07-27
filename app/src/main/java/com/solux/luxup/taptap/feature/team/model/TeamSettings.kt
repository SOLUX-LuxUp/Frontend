package com.solux.luxup.taptap.feature.team.model

/**
 * GET /api/teams/{team_id}/settings
 *
 * 팀원 누구나 조회 가능하다. 403 은 "팀 미가입 유저" 뿐이므로
 * 팀장 권한 없음 분기는 필요 없다.
 * 팀장 여부는 [isOwner] 로 프론트에서 판정한다.
 */
data class TeamSettings(
    val teamId: Long,
    val teamName: String,
    val teamImageUrl: String?,
    val iconName: String?,
    val iconColor: String?,
    val inviteCode: String,
    val maxMember: Int,
    val memberCount: Int,
    val buttonCreatePermission: TeamButtonPermission,
    val buttonEditPermission: TeamButtonPermission,
    val buttonDeletePermission: TeamButtonPermission,
    val ownerUserId: Long,
    /**
     * 팀 알림 마스터 ON/OFF.
     * 갱신은 PATCH /api/teams/{team_id}/notification — body 없는 토글 방식이라
     * 요청 시 값을 보내지 않고, 응답의 isEnabled 로 최종 상태를 맞춘다.
     */
    val notificationEnabled: Boolean,
    /** 팀 삭제 유예 상태 — 삭제 요청 후 3일(72시간) 동안 true, 실제 하드 삭제될 예정 시각 함께 내려옴 */
    val isDeleting: Boolean = false,
    val scheduledDeletionAt: String? = null,
) {
    /** 팀장 여부는 응답의 ownerUserId 와 내 userId 비교로 판정한다. */
    fun isOwner(currentUserId: Long): Boolean = ownerUserId == currentUserId
}

/**
 * 버튼 생성 / 수정 / 삭제 권한.
 *
 * - 생성: anyone / leader_only
 * - 수정: creator_or_leader / leader_only
 * - 삭제: creator_or_leader / leader_only
 *
 * 필드마다 허용 값이 달라서 enum 은 하나로 두고 선택지만 아래 목록으로 나눈다.
 *
 * PATCH /api/teams/{team_id}/settings 는 부분 업데이트다 —
 * 바뀐 필드 하나만 보내면 나머지는 서버가 기존 값으로 유지한다.
 */
enum class TeamButtonPermission(val raw: String, val label: String) {
    ANYONE("anyone", "누구나"),
    CREATOR_OR_LEADER("creator_or_leader", "생성자, 팀장"),
    LEADER_ONLY("leader_only", "팀장만");

    companion object {
        fun from(raw: String?): TeamButtonPermission =
            values().firstOrNull { it.raw == raw } ?: LEADER_ONLY

        /** 버튼 생성 권한 선택지 */
        val createOptions = listOf(ANYONE, LEADER_ONLY)

        /** 버튼 수정 · 삭제 권한 선택지 */
        val editOptions = listOf(CREATOR_OR_LEADER, LEADER_ONLY)
    }
}

/**
 * 팀 최대 인원 정책. 5단위 스텝, 5~30명.
 * 서버는 이 범위를 벗어나거나 현재 인원보다 작으면 400 을 준다.
 */
object TeamSizePolicy {
    const val MIN = 5
    const val MAX = 30
    const val STEP = 5

    val options: List<Int> = (MIN..MAX step STEP).toList()

    /** 현재 인원보다 작게는 못 줄인다 — 선택 가능한 값만 걸러낸다. */
    fun selectableOptions(memberCount: Int): List<Int> =
        options.filter { it >= memberCount }

    fun isValid(value: Int, memberCount: Int): Boolean =
        value in options && value >= memberCount
}