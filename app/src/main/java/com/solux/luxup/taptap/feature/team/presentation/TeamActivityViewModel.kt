package com.solux.luxup.taptap.feature.team.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.solux.luxup.taptap.feature.team.data.mockTeamButtons
import com.solux.luxup.taptap.feature.team.model.TeamButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 팀 활동 탭 — 팀 공유 버튼 목록.
 *
 *  - GET    /api/teams/{teamId}/buttons                        목록 (8.1.1)
 *  - POST   /api/teams/{teamId}/buttons/{buttonId}/records     탭 기록 (8.1.5)
 *  - DELETE /api/teams/{teamId}/buttons/{buttonId}             삭제 (8.1.2)
 */
class TeamActivityViewModel(
    private val teamId: Long,
    val currentUserId: Long,
) : ViewModel() {

    var buttons by mutableStateOf<List<TeamButton>>(emptyList())
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
            // TODO: GET /api/teams/{teamId}/buttons
            delay(200)
            buttons = mockTeamButtons
            isLoading = false
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

    companion object {
        fun factory(teamId: Long, currentUserId: Long) = viewModelFactory {
            initializer { TeamActivityViewModel(teamId, currentUserId) }
        }
    }
}