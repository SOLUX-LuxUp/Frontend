package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.model.TeamButton
import com.solux.luxup.taptap.feature.team.model.TeamButtonCategory
import com.solux.luxup.taptap.feature.team.model.TeamButtonSuggestion
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val RECORD_CANCEL_WINDOW_MILLIS = 3_000L

/**
 * 팀 활동 탭 — 팀 공유 버튼 목록.
 *
 *  - GET    /api/teams/{teamId}/buttons                        목록 (8.1.1)
 *  - POST   /api/teams/{teamId}/buttons/{buttonId}/records     탭 기록 (8.1.5)
 *  - DELETE /api/teams/{teamId}/buttons/{buttonId}             삭제 (8.1.2)
 *  - GET    /api/teams/{teamId}/template/suggestions           추천 버튼 목록
 */
@HiltViewModel(assistedFactory = TeamActivityViewModel.Factory::class)
class TeamActivityViewModel @AssistedInject constructor(
    @Assisted("teamId") private val teamId: Long,
    @Assisted("currentUserId") val currentUserId: Long,
    private val teamRepository: TeamRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("teamId") teamId: Long,
            @Assisted("currentUserId") currentUserId: Long,
        ): TeamActivityViewModel
    }

    var buttons by mutableStateOf<List<TeamButton>>(emptyList())
        private set

    var suggestions by mutableStateOf<List<TeamButtonSuggestion>>(emptyList())
        private set

    /** GET /api/teams/{team_id}/buttons/categories — 필터 드롭다운용 */
    var categories by mutableStateOf<List<TeamButtonCategory>>(emptyList())
        private set

    /** 팀장 여부 — 탭 권한 없음 안내 문구를 팀장/멤버로 갈라 보여주는 데 쓴다 */
    var isTeamOwner by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(true)
        private set

    /** 토스트로 한 번 노출하고 소비 */
    var toastMessage by mutableStateOf<String?>(null)
        private set

    /** 모달로 안내할 에러 */
    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** 연타로 기록이 중복 생성되는 것을 막는다 */
    private var isRecording = false

    private data class PendingRecord(val teamButtonId: Long, val recordId: Long)

    /** 방금 남긴 기록 — "기록 완료!" 취소 배너가 이걸로 노출 여부·취소 대상을 판단한다 */
    private var pendingRecord by mutableStateOf<PendingRecord?>(null)
    val showRecordCompleteBanner: Boolean
        get() = pendingRecord != null
    private var recordCancelWindowJob: Job? = null

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            isLoading = true
            teamRepository.listButtons(teamId)
                .onSuccess { buttons = it }
                .onFailure { errorMessage = it.message ?: "버튼 목록을 불러오지 못했어요." }

            // 건너뛴 팀이면 빈 배열이 온다. 추천 목록은 활동 탭 핵심 기능이 아니라
            // 조회 실패 시 에러 모달 없이 빈 목록으로 조용히 넘어간다.
            teamRepository.getTemplateSuggestions(teamId)
                .onSuccess { suggestions = it }
                .onFailure { suggestions = emptyList() }

            teamRepository.getSettings(teamId)
                .onSuccess { isTeamOwner = it.ownerUserId == currentUserId }

            loadCategories()

            isLoading = false
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            teamRepository.getButtonCategories(teamId)
                .onSuccess { categories = it }
        }
    }

    // ---- 카테고리 관리 ----

    fun createCategory(name: String) {
        viewModelScope.launch {
            teamRepository.createButtonCategory(teamId, name)
                .onSuccess { loadCategories() }
                .onFailure { errorMessage = it.message ?: "카테고리를 만들지 못했어요." }
        }
    }

    fun renameCategory(categoryId: Long, name: String) {
        viewModelScope.launch {
            teamRepository.renameButtonCategory(teamId, categoryId, name)
                .onSuccess { loadCategories() }
                .onFailure { errorMessage = it.message ?: "카테고리 이름을 변경하지 못했어요." }
        }
    }

    fun deleteCategory(categoryId: Long, deleteButtonsToo: Boolean) {
        viewModelScope.launch {
            teamRepository.deleteButtonCategory(teamId, categoryId, deleteButtonsToo)
                .onSuccess { load() }
                .onFailure { errorMessage = it.message ?: "카테고리를 삭제하지 못했어요." }
        }
    }

    /**
     * 추천 버튼 하나를 골라 팀 공유 버튼으로 생성한다.
     * POST /api/teams/{teamId}/buttons
     *  body: buttonName·iconName·iconColor·categoryId (description=null, tapPermission="all")
     *  생성 성공 시 다음 조회부터 목록에서 자동 제외된다.
     */
    fun createFromSuggestion(suggestion: TeamButtonSuggestion) {
        viewModelScope.launch {
            teamRepository.createButton(teamId, suggestion)
                .onSuccess {
                    toastMessage = "'${suggestion.buttonName}' 버튼을 추가했어요"
                    load()
                }
                .onFailure {
                    errorMessage = "버튼을 추가하지 못했어요.\n잠시 후 다시 시도해 주세요."
                }
        }
    }

    /**
     * 짧게 누르기 = 탭 기록.
     * 메모·이모지는 보내지 않고, 타임라인에서 나중에 추가한다.
     * 기록 성공 시 화면 상단에 "기록 완료!" 취소 배너를 3초간 띄운다 — 그 안에 취소하지 않으면
     * 배너는 그냥 사라지고 기록은 그대로 유지된다(개인 파트 RecordCompleteBanner와 동일한 패턴).
     */
    fun recordTap(button: TeamButton) {
        // 권한 없는 버튼은 화면에서 안내 모달로 막지만, 여기서도 한 번 더 확인
        if (!button.hasTapPermission || isRecording) return

        viewModelScope.launch {
            isRecording = true

            teamRepository.createRecord(teamId, button.teamButtonId)
                .onSuccess { result ->
                    pendingRecord = PendingRecord(teamButtonId = result.teamButtonId, recordId = result.recordId)
                    load()

                    recordCancelWindowJob?.cancel()
                    recordCancelWindowJob = viewModelScope.launch {
                        delay(RECORD_CANCEL_WINDOW_MILLIS)
                        pendingRecord = null
                    }
                }
                .onFailure {
                    errorMessage = "기록하지 못했어요.\n잠시 후 다시 시도해 주세요."
                }

            isRecording = false
        }
    }

    /** "기록 완료!" 배너의 "취소" — 방금 남긴 기록을 그대로 삭제한다 */
    fun cancelPendingRecord() {
        val pending = pendingRecord ?: return
        recordCancelWindowJob?.cancel()
        pendingRecord = null

        viewModelScope.launch {
            teamRepository.deleteRecord(teamId, pending.teamButtonId, pending.recordId)
                .onSuccess { load() }
                .onFailure { errorMessage = it.message ?: "기록 취소에 실패했어요." }
        }
    }

    fun deleteButton(button: TeamButton) {
        viewModelScope.launch {
            teamRepository.deleteButton(teamId, button.teamButtonId)
                .onSuccess {
                    toastMessage = "버튼을 삭제했어요"
                    load()
                }
                .onFailure {
                    errorMessage = it.message ?: "버튼을 삭제하지 못했어요.\n잠시 후 다시 시도해 주세요."
                }
        }
    }

    fun refresh() = load()

    fun consumeToast() {
        toastMessage = null
    }

    fun consumeError() {
        errorMessage = null
    }
}