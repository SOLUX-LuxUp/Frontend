package com.solux.luxup.taptap.feature.team.model

data class Team(
    val id: Long,
    val name: String,
    val isFavorite: Boolean,
    val memberCount: Int,
    val recentRecord: RecentRecord?,   // 최근 기록 없을 수도 있어서 nullable
    val updatedMemberCount: Int = 0      // ← 추가
)

data class RecentRecord(
    val buttonName: String,   // 예: 공지 업로드
    val timeAgo: String       // 예: 3분 전
)