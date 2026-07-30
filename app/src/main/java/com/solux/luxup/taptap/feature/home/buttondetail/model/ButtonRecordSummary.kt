package com.solux.luxup.taptap.feature.home.buttondetail.model

/** GET /api/buttons/{button_id}/records/summary 응답 모델 — 버튼 상세 화면의 "최근 기록" 배너 */
data class ButtonRecordSummary(
    val buttonId: Long,
    val lastRecordedAt: String?, // ISO-8601 (UTC), 예: "2026-07-21T02:37:00Z"
    val todayCount: Long,
    val totalCount: Long,
)