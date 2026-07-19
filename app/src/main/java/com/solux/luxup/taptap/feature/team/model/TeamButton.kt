package com.solux.luxup.taptap.feature.team.model

data class TeamButtonsResponse(
    val buttons: List<TeamButton>
)

data class TeamButton(
    val teamButtonId: Long,
    val buttonName: String,
    val iconName: String,          // "exercise", "book" — 아이콘 매핑용
    val iconColor: String,         // "#FF5733" — 아이콘 색
    val tapPermission: String,     // "all" 등
    val categoryId: Long?,
    val categoryName: String?,
    val hasTapPermission: Boolean,
    val latestRecord: ButtonRecord?
)

data class ButtonRecord(
    val recordedAt: String,
    val recordedBy: List<MemberProfile>,
    val recordedByCount: Int
)