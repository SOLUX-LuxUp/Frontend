package com.solux.luxup.taptap.feature.team.presentation.button.timeline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.solux.luxup.taptap.feature.team.data.MockTeamButtonTimeline
import com.solux.luxup.taptap.feature.team.model.TeamButtonLatest
import com.solux.luxup.taptap.feature.team.model.TeamButtonTimelineRecord
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 팀 공유 버튼 타임라인 (8.1.5 / 8.1.7).
 *
 * 두 API를 함께 쓴다.
 *  - GET .../records/latest    : 버튼 헤더와 최근 기록 배너
 *  - GET .../records/timeline  : 커서 기반 30건씩
 */
class TeamButtonTimelineViewModel(
    private val teamId: Long,
    private val teamButtonId: Long,
    val currentUserId: Long,
) : ViewModel() {

    var latest by mutableStateOf<TeamButtonLatest?>(null)
        private set

    var records by mutableStateOf<List<TeamButtonTimelineRecord>>(emptyList())
        private set

    var hasMore by mutableStateOf(false)
        private set

    private var nextCursor: Long? = null

    var isLoading by mutableStateOf(true)
        private set

    /** 더보기 진행 중 — 중복 요청 방지 */
    var isLoadingMore by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            isLoading = true

            // TODO: GET /api/teams/{teamId}/buttons/{teamButtonId}/records/latest
            // TODO: GET /api/teams/{teamId}/buttons/{teamButtonId}/records/timeline?limit=30
            delay(200)

            latest = MockTeamButtonTimeline.latest
            records = MockTeamButtonTimeline.records
            hasMore = MockTeamButtonTimeline.timeline.hasMore
            nextCursor = MockTeamButtonTimeline.timeline.nextCursor

            isLoading = false
        }
    }

    /** 더보기 — 30건씩 확장 */
    fun loadMore() {
        if (isLoadingMore || !hasMore) return
        val cursor = nextCursor ?: return

        viewModelScope.launch {
            isLoadingMore = true

            // TODO: GET .../records/timeline?cursor=$cursor&limit=30
            //  응답의 records를 기존 목록 뒤에 이어 붙이고 hasMore/nextCursor 갱신
            delay(200)
            hasMore = false
            nextCursor = null

            isLoadingMore = false
        }
    }

    /**
     * 기록 삭제.
     * DELETE /api/teams/{teamId}/buttons/{teamButtonId}/records/{record.recordId}
     *
     * 에러 정책 (연동 시 상태 코드로 분기):
     *  - 404: 화면을 열어둔 사이 버튼이 삭제됨 → "삭제된 버튼이에요" 안내 후 목록으로 back
     *  - 403: 본인 기록이 아님 → "본인 기록만 삭제할 수 있어요" (그 자리 유지)
     *  - 그 외: "잠시 후 다시 시도해 주세요"
     *
     *  참고: 본인 기록에서만 액션 메뉴가 열리므로 403은 정상 플로우에선 발생하지 않지만,
     *        상태가 바뀌는 경우를 대비해 방어적으로 처리한다.
     */
    fun deleteRecord(record: TeamButtonTimelineRecord) {
        viewModelScope.launch {
            // TODO: 실제 호출로 교체
            runCatching { delay(200) }
                .onSuccess { load() }
                .onFailure { errorMessage = "기록을 삭제하지 못했어요.\n잠시 후 다시 시도해 주세요." }
        }
    }

    /**
     * 메모·이모지 저장.
     * PATCH /api/teams/{teamId}/buttons/{teamButtonId}/records/{record.recordId}/detail
     *  body: { memo, emoji } — 키 생략 시 기존 값 유지, null이면 삭제, 값이 있으면 수정.
     *  둘 다 키가 없으면 400이므로, 최소 한쪽은 키를 포함해 보낸다.
     *
     * 에러 정책 (연동 시 상태 코드로 분기):
     *  - 400: memo·emoji 둘 다 없음 → 전송 전에 막아 발생하지 않도록 한다
     *  - 404: 버튼이 삭제됨 → "삭제된 버튼이에요" 안내 후 목록으로 back
     *  - 403: 본인 기록 아님 → "본인 기록만 수정할 수 있어요"
     *  - 그 외: "잠시 후 다시 시도해 주세요"
     */
    fun saveMemo(record: TeamButtonTimelineRecord, memo: String?, emoji: String?) {
        viewModelScope.launch {
            // TODO: 실제 호출로 교체
            runCatching { delay(200) }
                .onSuccess { load() }
                .onFailure { errorMessage = "메모를 저장하지 못했어요.\n잠시 후 다시 시도해 주세요." }
        }
    }

    fun consumeError() {
        errorMessage = null
    }

    companion object {
        fun factory(teamId: Long, teamButtonId: Long, currentUserId: Long) = viewModelFactory {
            initializer { TeamButtonTimelineViewModel(teamId, teamButtonId, currentUserId) }
        }
    }
}