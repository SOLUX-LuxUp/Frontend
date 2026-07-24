package com.solux.luxup.taptap.feature.team.model

/**
 * GET /api/teams/{team_id}/template/suggestions
 *
 * 아직 생성되지 않은 "추천"이라 teamButtonId가 없다.
 * 선택 시 이 값들을 그대로 POST /api/teams/{team_id}/buttons 에 담아 호출
 * (description = null, tapPermission = "all")
 *
 * 팀이 템플릿을 선택하지 않았다면 빈 배열로 내려온다.
 * "No Category" 프리셋은 categoryId / categoryName이 null.
 */
data class TeamButtonSuggestion(
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
    val categoryId: Long?,
    val categoryName: String?,
)