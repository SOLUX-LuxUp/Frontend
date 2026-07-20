package com.solux.luxup.taptap.feature.team.model

data class Team(
    val teamId: Long,
    val teamName: String,
    val teamImageUrl: String?,      // 이미지로 설정 시
    val iconName: String?,          // 아이콘으로 설정 시 ("exercise")
    val iconColor: String?,         // 아이콘 색상 ("red")
    val isFavorite: Boolean,
    val maxMember: Int,
    val memberCount: Int,
    val memberProfiles: List<MemberProfile>,
    val latestRecord: LatestRecord?,    // 기록 없을 수 있어 nullable
    val recentUpdatedMembers: List<MemberProfile>,   // 추가: 업데이트 바용 (백엔드 추가 예정)
    val updatedAt: String?
)

data class MemberProfile(
    val userId: Long,
    val displayName: String,
    val profileImageUrl: String?
)

data class LatestRecord(
    val teamButtonId: Long,
    val buttonName: String,
    val iconName: String,        // "exercise" — 버튼 아이콘 매핑용
    val iconColor: String,       // "#FF5733" — 아이콘 색
    val recordedAt: String
)