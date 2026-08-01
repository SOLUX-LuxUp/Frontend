package com.solux.luxup.taptap.feature.team.presentation.button.timeline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.home.buttondetail.data.CustomEmojiStore
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.model.TeamButtonLatest
import com.solux.luxup.taptap.feature.team.model.TeamButtonTimelineRecord
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 30

/**
 * 팀 공유 버튼 타임라인 (8.1.5 / 8.1.7).
 *
 * 두 API를 함께 쓴다.
 *  - GET .../records/latest    : 버튼 헤더와 최근 기록 배너
 *  - GET .../records/timeline  : 커서 기반 30건씩
 */
@HiltViewModel(assistedFactory = TeamButtonTimelineViewModel.Factory::class)
class TeamButtonTimelineViewModel @AssistedInject constructor(
    @Assisted("teamId") private val teamId: Long,
    @Assisted("teamButtonId") private val teamButtonId: Long,
    @Assisted("currentUserId") val currentUserId: Long,
    private val teamRepository: TeamRepository,
    private val customEmojiStore: CustomEmojiStore,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("teamId") teamId: Long,
            @Assisted("teamButtonId") teamButtonId: Long,
            @Assisted("currentUserId") currentUserId: Long,
        ): TeamButtonTimelineViewModel
    }

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

    /** 메모/이모지 다이얼로그의 이모지 피커로 새로 고른 이모지 — 개인 홈과 기기 전역으로 공유된다 */
    var customEmojis by mutableStateOf(customEmojiStore.getCustomEmojis())
        private set

    init {
        load()
    }

    fun addCustomEmoji(emoji: String) {
        customEmojiStore.addCustomEmoji(emoji)
        customEmojis = customEmojiStore.getCustomEmojis()
    }

    private fun load() {
        viewModelScope.launch {
            isLoading = true

            teamRepository.getLatestRecord(teamId, teamButtonId)
                .onSuccess { latest = it }
                .onFailure { errorMessage = it.message ?: "버튼 정보를 불러오지 못했어요." }

            teamRepository.getTimeline(teamId, teamButtonId, cursor = null, limit = PAGE_SIZE)
                .onSuccess { timeline ->
                    records = timeline.records
                    hasMore = timeline.hasMore
                    nextCursor = timeline.nextCursor
                }
                .onFailure { errorMessage = it.message ?: "기록을 불러오지 못했어요." }

            isLoading = false
        }
    }

    /** 더보기 — 30건씩 확장 */
    fun loadMore() {
        if (isLoadingMore || !hasMore) return
        val cursor = nextCursor ?: return

        viewModelScope.launch {
            isLoadingMore = true

            teamRepository.getTimeline(teamId, teamButtonId, cursor = cursor, limit = PAGE_SIZE)
                .onSuccess { timeline ->
                    records = records + timeline.records
                    hasMore = timeline.hasMore
                    nextCursor = timeline.nextCursor
                }
                .onFailure { errorMessage = it.message ?: "기록을 불러오지 못했어요." }

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
            teamRepository.deleteRecord(teamId, teamButtonId, record.recordId)
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
            teamRepository.updateRecordDetail(teamId, teamButtonId, record.recordId, memo, emoji)
                .onSuccess { load() }
                .onFailure { errorMessage = "메모를 저장하지 못했어요.\n잠시 후 다시 시도해 주세요." }
        }
    }

    fun consumeError() {
        errorMessage = null
    }
}
