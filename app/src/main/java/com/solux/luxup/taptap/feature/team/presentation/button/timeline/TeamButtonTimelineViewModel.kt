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
     * TODO(백엔드): 팀 기록 삭제 API가 명세에 없음.
     *  개인은 DELETE /api/buttons/{button_id}/records/{record_id} 가 있으나 팀 버전 확인 필요.
     */
    fun deleteRecord(record: TeamButtonTimelineRecord) {
        viewModelScope.launch {
            runCatching { delay(200) }
                .onSuccess { load() }
                .onFailure { errorMessage = "기록을 삭제하지 못했어요.\n잠시 후 다시 시도해 주세요." }
        }
    }

    /**
     * 메모·이모지 저장.
     * TODO(백엔드): 팀 기록 상세 수정 API가 명세에 없음.
     *  개인은 PATCH .../records/{record_id}/detail 가 있으나 팀 버전 확인 필요.
     */
    fun saveMemo(record: TeamButtonTimelineRecord, memo: String?, emoji: String?) {
        viewModelScope.launch {
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