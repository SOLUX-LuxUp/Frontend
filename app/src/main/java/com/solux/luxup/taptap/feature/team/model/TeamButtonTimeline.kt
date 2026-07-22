package com.solux.luxup.taptap.feature.team.model

/**
 * GET /api/teams/{team_id}/buttons/{team_button_id}/records/timeline (8.1.5)
 * 커서 기반 페이지네이션 — cursor = 마지막 recordId, limit 기본 30
 */
data class TeamButtonTimeline(
    val records: List<TeamButtonTimelineRecord>,
    val hasMore: Boolean,
    val nextCursor: Long?,
)

data class TeamButtonTimelineRecord(
    val recordId: Long,
    val recordedAt: String,
    val memo: String?,
    val emoji: String?,
    /** 기록한 유저 — 팀 타임라인은 개인과 달리 프로필을 명시한다 */
    val recordedBy: MemberProfile,
) {
    /** 본인 기록만 메모 추가·삭제가 가능하다 */
    fun isMine(currentUserId: Long): Boolean = recordedBy.userId == currentUserId
}

/**
 * GET .../records/latest (8.1.7)
 * 타임라인 화면 상단 배너와 버튼 헤더에 사용
 */
data class TeamButtonLatest(
    val teamButtonId: Long,
    val buttonName: String,
    val iconName: String,
    val iconColor: String,
    val latestRecord: TeamButtonTimelineRecord?,
)

/** 일자별 파티셔닝 결과 */
data class TimelineDayGroup(
    val label: String,
    val records: List<TeamButtonTimelineRecord>,
)