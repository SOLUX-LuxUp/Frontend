package com.solux.luxup.taptap.feature.team.data

import com.solux.luxup.taptap.core.ui.theme.IconColor
import com.solux.luxup.taptap.feature.team.model.TeamButtonCategory

/**
 * 팀 버튼 생성 플로우 목데이터.
 * 실 API 연동 시 삭제 대상.
 */
object MockTeamButtonCreate {

    /**
     * TODO(백엔드): 팀 버튼 카테고리 목록 조회 API 부재.
     * 명세서에는 개인 버튼용 /api/buttons/categories 만 존재.
     */
    val categories: List<TeamButtonCategory> = listOf(
        TeamButtonCategory(1L, "가족", IconColor.ORANGE, 0),
        TeamButtonCategory(2L, "친구", IconColor.CYAN, 1),
        TeamButtonCategory(3L, "연인", IconColor.PINK, 2),
        TeamButtonCategory(4L, "No Category", IconColor.GREY, 3),
    )
}