package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solux.luxup.taptap.feature.team.data.TeamRepository
import com.solux.luxup.taptap.feature.team.data.mockTeamButtons
import com.solux.luxup.taptap.feature.team.model.TeamButton
import com.solux.luxup.taptap.feature.team.model.TeamButtonSuggestion
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            isLoading = true
            // TODO: GET /api/teams/{teamId}/buttons (팀 공유버튼 API 브랜치에서 교체)
            delay(200)
            buttons = mockTeamButtons

            // 건너뛴 팀이면 빈 배열이 온다. 추천 목록은 활동 탭 핵심 기능이 아니라
            // 조회 실패 시 에러 모달 없이 빈 목록으로 조용히 넘어간다.
            teamRepository.getTemplateSuggestions(teamId)
                .onSuccess { suggestions = it }
                .onFailure { suggestions = emptyList() }

            isLoading = false
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
            // TODO: POST /api/teams/{teamId}/buttons 로 생성 후 재조회
            runCatching { delay(200) }
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
     */
    fun recordTap(button: TeamButton) {
        // 권한 없는 버튼은 화면에서 안내 모달로 막지만, 여기서도 한 번 더 확인
        if (!button.hasTapPermission || isRecording) return

        viewModelScope.launch {
            isRecording = true

            // TODO: POST /api/teams/{teamId}/buttons/{button.teamButtonId}/records
            //  body 없이 호출 (memo·emoji는 optional)
            //  201 성공 → 목록 재조회로 "몇 분 전 기록"과 최근 기록 배너 갱신
            //  403 탭 권한 없음 → errorMessage
            runCatching { delay(200) }
                .onSuccess {
                    toastMessage = "기록했어요"
                    load()
                }
                .onFailure {
                    errorMessage = "기록하지 못했어요.\n잠시 후 다시 시도해 주세요."
                }

            isRecording = false
        }
    }

    fun deleteButton(button: TeamButton) {
        viewModelScope.launch {
            // TODO: DELETE /api/teams/{teamId}/buttons/{button.teamButtonId}
            //  403이면 삭제 권한 없음 안내
            runCatching { delay(200) }
                .onSuccess {
                    toastMessage = "버튼을 삭제했어요"
                    load()
                }
                .onFailure {
                    errorMessage = "버튼을 삭제하지 못했어요.\n잠시 후 다시 시도해 주세요."
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