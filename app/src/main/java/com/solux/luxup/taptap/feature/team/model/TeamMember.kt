package com.solux.luxup.taptap.feature.team.model

data class TeamMember(
    val userId: Long,
    val displayName: String,
    val profileImageUrl: String?,
    val role: TeamMemberRole,
    val joinedAt: String,
    val latestRecord: MemberLatestRecord?   // 프로퍼티 이름은 그대로, 타입만 분리
)

data class MemberLatestRecord(             // 기존 LatestRecord와 충돌 방지
    val buttonName: String,
    val recordedAt: String                 // ISO-8601
)

enum class TeamMemberRole {
    OWNER,
    MEMBER;

    companion object {
        fun from(raw: String): TeamMemberRole =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: MEMBER
    }
}