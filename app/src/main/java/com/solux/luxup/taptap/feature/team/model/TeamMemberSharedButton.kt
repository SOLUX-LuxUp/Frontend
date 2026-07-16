package com.solux.luxup.taptap.feature.team.model

// 8.2.4 GET /api/teams/{team_id}/members/profile 의 buttons[]
data class TeamMemberSharedButton(
    val buttonId: Long,
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
    val categoryId: Long?,          // null 가능 (미분류)
    val categoryName: String?,      // null 가능
    val isShared: Boolean
)
// 이 후에 api 명세 수정 후 : 카테고리 필드랑 iconcolor/name 추가할듯