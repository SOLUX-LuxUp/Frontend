package com.solux.luxup.taptap.feature.team.model

data class Team(
    val teamId: Long,
    val teamName: String,
    val teamImageUrl: String?,          // 이미지 없을 수 있어 nullable
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
    val iconKey: String,          // 추가: 버튼 아이콘 매핑용 (백엔드 추가 예정)
    val recordedAt: String              // "2025-05-23T14:32:00" 형태
)